package kko.traveldiary_api.shared.idempotency;

/**
 * 멱등키 저장 상태의 스냅샷.
 * responseStatus/responseBody 는 COMPLETED 상태에서만 채워진다.
 */
public record IdempotencyRecord(Status status, Integer responseStatus, String responseBody) {

    public enum Status {
        IN_PROGRESS, COMPLETED
    }

    public boolean isCompleted() {
        return status == Status.COMPLETED;
    }
}
