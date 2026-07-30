package kko.traveldiary_api.shared.idempotency;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 멱등키 저장소 포트. 구현은 인프라 어댑터가 담당한다.
 */
public interface IdempotencyStore {

    /**
     * 키를 선점(IN_PROGRESS 로 INSERT)한다.
     * 이미 존재하면 false 를 반환한다. (유니크 제약이 동시성 게이트)
     */
    boolean reserve(String key, Long memberId);

    Optional<IdempotencyRecord> find(String key);

    /** 최초 요청 처리가 성공하면 응답 스냅샷을 저장하고 COMPLETED 로 전환한다. */
    void complete(String key, int responseStatus, String responseBody);

    /** 처리가 실패하면 예약을 취소해 재시도를 허용한다. */
    void release(String key);

    /** 주어진 상태이면서 threshold 이전에 생성된 키를 일괄 삭제하고, 삭제 건수를 반환한다. */
    int deleteOlderThan(IdempotencyRecord.Status status, LocalDateTime threshold);
}
