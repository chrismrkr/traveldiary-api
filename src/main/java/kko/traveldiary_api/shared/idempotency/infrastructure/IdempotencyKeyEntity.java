package kko.traveldiary_api.shared.idempotency.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public static IdempotencyKeyEntity reserve(String idempotencyKey, Long memberId) {
        IdempotencyKeyEntity entity = new IdempotencyKeyEntity();
        entity.idempotencyKey = idempotencyKey;
        entity.memberId = memberId;
        entity.createdAt = LocalDateTime.now();
        return entity;
    }
}
