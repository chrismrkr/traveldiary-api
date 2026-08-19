package kko.traveldiary_api.shared.idempotency;

import java.time.LocalDateTime;

/**
 * 멱등키 저장소 포트. 구현은 인프라 어댑터가 담당한다.
 */
public interface IdempotencyStore {

    /**
     * 키를 선점(INSERT)한다. 이미 존재하면 false 를 반환한다.
     * 유니크 제약이 동시성 게이트이므로, 이 INSERT 하나로 중복 실행 판정이 끝난다.
     */
    boolean reserve(String key, Long memberId);

    /** threshold 이전에 생성된 키를 일괄 삭제하고, 삭제 건수를 반환한다. */
    int deleteOlderThan(LocalDateTime threshold);
}
