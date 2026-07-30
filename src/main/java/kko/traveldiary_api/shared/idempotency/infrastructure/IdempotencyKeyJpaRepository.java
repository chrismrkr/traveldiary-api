package kko.traveldiary_api.shared.idempotency.infrastructure;

import kko.traveldiary_api.shared.idempotency.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface IdempotencyKeyJpaRepository extends JpaRepository<IdempotencyKeyEntity, Long> {
    Optional<IdempotencyKeyEntity> findByIdempotencyKey(String idempotencyKey);

    @Modifying
    @Query("delete from IdempotencyKeyEntity e where e.status = :status and e.createdAt < :threshold")
    int deleteByStatusAndCreatedAtBefore(@Param("status") IdempotencyRecord.Status status,
                                         @Param("threshold") LocalDateTime threshold);
}
