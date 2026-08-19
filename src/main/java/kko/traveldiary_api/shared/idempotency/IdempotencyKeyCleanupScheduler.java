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
 * 보관 기간이 지난 키를 삭제해 테이블 무한 증식을 막는다.
 * TTL 이 지나면 같은 키로 다시 요청할 수 있게 되므로, 클라이언트의 재시도 주기보다는 충분히 길어야 한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IdempotencyKeyCleanupScheduler {
    private final IdempotencyStore store;

    /** 멱등키 보관 시간 (기본 24시간) */
    @Value("${app.idempotency.ttl:PT24H}")
    private Duration ttl;

    @Scheduled(fixedDelayString = "${app.idempotency.cleanup-interval:PT10M}",
            initialDelayString = "${app.idempotency.cleanup-interval:PT10M}")
    public void cleanup() {
        int deleted = store.deleteOlderThan(LocalDateTime.now().minus(ttl));

        if (deleted > 0) {
            log.info("[Idempotency] cleaned up {} expired key(s)", deleted);
        }
    }
}
