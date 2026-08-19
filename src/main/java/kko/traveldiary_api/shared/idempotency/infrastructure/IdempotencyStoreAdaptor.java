package kko.traveldiary_api.shared.idempotency.infrastructure;

import kko.traveldiary_api.shared.idempotency.IdempotencyStore;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
    @Transactional
    public int deleteOlderThan(LocalDateTime threshold) {
        return repository.deleteByCreatedAtBefore(threshold);
    }
}
