package kko.traveldiary_api.shared.idempotency;

import kko.traveldiary_api.shared.idempotency.infrastructure.IdempotencyKeyJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class IdempotencyKeyCleanupTest {

    @Autowired
    IdempotencyStore store;

    @Autowired
    IdempotencyKeyJpaRepository repository;

    @BeforeEach
    @AfterEach
    void clean() {
        repository.deleteAll();
    }

    private String reservedKey() {
        String key = UUID.randomUUID().toString();
        store.reserve(key, 1L);
        return key;
    }

    @Test
    @DisplayName("threshold 이전에 생성된 IN_PROGRESS 키만 삭제하고 COMPLETED 는 남긴다")
    void deletesStaleInProgressOnly() {
        String inProgress = reservedKey();
        String completed = reservedKey();
        store.complete(completed, 200, "{}");

        // 방금 만든 두 건 모두 threshold(1분 뒤) 이전이지만 상태로 선별한다.
        int deleted = store.deleteOlderThan(
                IdempotencyRecord.Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(1));

        assertThat(deleted).isEqualTo(1);
        assertThat(repository.findByIdempotencyKey(inProgress)).isEmpty();
        assertThat(repository.findByIdempotencyKey(completed)).isPresent();
    }

    @Test
    @DisplayName("threshold 이전에 생성된 COMPLETED 키를 삭제한다")
    void deletesExpiredCompleted() {
        String completed = reservedKey();
        store.complete(completed, 200, "{}");

        int deleted = store.deleteOlderThan(
                IdempotencyRecord.Status.COMPLETED, LocalDateTime.now().plusMinutes(1));

        assertThat(deleted).isEqualTo(1);
        assertThat(repository.findByIdempotencyKey(completed)).isEmpty();
    }

    @Test
    @DisplayName("threshold 이후에 생성된(=아직 만료 전) 키는 삭제하지 않는다")
    void keepsFreshKeys() {
        String inProgress = reservedKey();

        // threshold 를 과거로 두면 방금 만든 키는 대상이 아니다.
        int deleted = store.deleteOlderThan(
                IdempotencyRecord.Status.IN_PROGRESS, LocalDateTime.now().minusMinutes(1));

        assertThat(deleted).isEqualTo(0);
        assertThat(repository.findByIdempotencyKey(inProgress)).isPresent();
    }
}
