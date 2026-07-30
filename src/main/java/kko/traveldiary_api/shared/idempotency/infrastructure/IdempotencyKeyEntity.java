package kko.traveldiary_api.shared.idempotency.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import kko.traveldiary_api.shared.idempotency.IdempotencyRecord;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "idempotency_key",
        uniqueConstraints = @UniqueConstraint(name = "uk_idempotency_key", columnNames = "idempotency_key"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IdempotencyKeyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "idempotency_key", nullable = false)
    private String idempotencyKey;

    @Column(name = "member_id")
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IdempotencyRecord.Status status;

    @Column(name = "response_status")
    private Integer responseStatus;

    @Column(name = "response_body", columnDefinition = "TEXT")
    private String responseBody;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public static IdempotencyKeyEntity reserve(String idempotencyKey, Long memberId) {
        IdempotencyKeyEntity entity = new IdempotencyKeyEntity();
        entity.idempotencyKey = idempotencyKey;
        entity.memberId = memberId;
        entity.status = IdempotencyRecord.Status.IN_PROGRESS;
        entity.createdAt = LocalDateTime.now();
        return entity;
    }

    public void complete(int responseStatus, String responseBody) {
        this.status = IdempotencyRecord.Status.COMPLETED;
        this.responseStatus = responseStatus;
        this.responseBody = responseBody;
    }

    public IdempotencyRecord toRecord() {
        return new IdempotencyRecord(status, responseStatus, responseBody);
    }
}
