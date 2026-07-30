package kko.traveldiary_api.shared.idempotency.infrastructure;

import kko.traveldiary_api.shared.idempotency.IdempotencyRecord;
import kko.traveldiary_api.shared.idempotency.IdempotencyStore;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class IdempotencyStoreAdaptor implements IdempotencyStore {
    private final IdempotencyKeyJpaRepository repository;

    @Override
    public boolean reserve(String key, Long memberId) {
        try {
            // saveAndFlush 로 즉시 INSERT → 유니크 제약 위반을 지금 감지한다.
            repository.saveAndFlush(IdempotencyKeyEntity.reserve(key, memberId));
            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }

    @Override
    public Optional<IdempotencyRecord> find(String key) {
        return repository.findByIdempotencyKey(key).map(IdempotencyKeyEntity::toRecord);
    }

    @Override
    public void complete(String key, int responseStatus, String responseBody) {
        repository.findByIdempotencyKey(key).ifPresent(entity -> {
            entity.complete(responseStatus, responseBody);
            repository.save(entity);
        });
    }

    @Override
    public void release(String key) {
        repository.findByIdempotencyKey(key).ifPresent(repository::delete);
    }

    @Override
    @Transactional
    public int deleteOlderThan(IdempotencyRecord.Status status, LocalDateTime threshold) {
        return repository.deleteByStatusAndCreatedAtBefore(status, threshold);
    }
}
