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
    @DisplayName("threshold 이전에 생성된 키를 삭제한다")
    void deletesExpiredKeys() {
        String first = reservedKey();
        String second = reservedKey();

        int deleted = store.deleteOlderThan(LocalDateTime.now().plusMinutes(1));

        assertThat(deleted).isEqualTo(2);
        assertThat(repository.findByIdempotencyKey(first)).isEmpty();
        assertThat(repository.findByIdempotencyKey(second)).isEmpty();
    }

    @Test
    @DisplayName("threshold 이후에 생성된(=아직 만료 전) 키는 삭제하지 않는다")
    void keepsFreshKeys() {
        String key = reservedKey();

        // threshold 를 과거로 두면 방금 만든 키는 대상이 아니다.
        int deleted = store.deleteOlderThan(LocalDateTime.now().minusMinutes(1));

        assertThat(deleted).isZero();
        assertThat(repository.findByIdempotencyKey(key)).isPresent();
    }

    @Test
    @DisplayName("TTL 이 지나 키가 삭제되면 같은 키로 다시 예약할 수 있다")
    void keyIsReusableAfterCleanup() {
        String key = reservedKey();
        assertThat(store.reserve(key, 1L)).isFalse();

        store.deleteOlderThan(LocalDateTime.now().plusMinutes(1));

        assertThat(store.reserve(key, 1L)).isTrue();
    }
}
