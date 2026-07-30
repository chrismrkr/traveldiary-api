package kko.traveldiary_api.shared.idempotency;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 멱등키 정리 스케줄러.
 * <ul>
 *   <li>IN_PROGRESS: complete 전에 중단(서버 다운 등)된 예약을 일정 시간 뒤 삭제해 무한 409 를 방지한다.</li>
 *   <li>COMPLETED: replay 보관 기간이 지난 키를 삭제해 테이블 무한 증식을 막는다.</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IdempotencyKeyCleanupScheduler {
    private final IdempotencyStore store;

    /** IN_PROGRESS 예약을 만료로 간주하는 시간 (기본 5분) */
    @Value("${app.idempotency.in-progress-ttl:PT5M}")
    private Duration inProgressTtl;

    /** COMPLETED 응답을 재생 가능하도록 보관하는 시간 (기본 24시간) */
    @Value("${app.idempotency.completed-ttl:PT24H}")
    private Duration completedTtl;

    @Scheduled(fixedDelayString = "${app.idempotency.cleanup-interval:PT10M}",
            initialDelayString = "${app.idempotency.cleanup-interval:PT10M}")
    public void cleanup() {
        LocalDateTime now = LocalDateTime.now();
        int staleReservations = store.deleteOlderThan(
                IdempotencyRecord.Status.IN_PROGRESS, now.minus(inProgressTtl));
        int expiredResponses = store.deleteOlderThan(
                IdempotencyRecord.Status.COMPLETED, now.minus(completedTtl));

        if (staleReservations + expiredResponses > 0) {
            log.info("[Idempotency] cleaned up {} stale reservation(s) + {} expired response(s)",
                    staleReservations, expiredResponses);
        }
    }
}
