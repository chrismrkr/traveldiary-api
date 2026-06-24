package kko.traveldiary_api.post.adaptor.infrastructure;

import kko.traveldiary_api.journey.adaptor.infrastructure.JourneyDatabaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestConstructor;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(JourneyDatabaseRepository.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
record PostDatabaseRepositoryTest(PostDatabaseRepository postDatabaseRepository, PostJpaRepository jpaRepository) {

    @BeforeEach
    void init() {
        jpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Post 엔티티를 저장할 수 있다")
    void save() {

    }

    @Test
    @DisplayName("Post 엔티티를 Id로 조회할 수 있다")
    void findById() {

    }

    @Test
    @DisplayName("Post 엔티티 목록을 CityVisitId로 조회할 수 있다")
    void findByCityVisitId() {

    }

    @Test
    @DisplayName("Post 엔티티를 Id로 삭제할 수 있다")
    void deleteById() {

    }

    @Test
    @DisplayName("Post 엔티티를 도메인 객체를 통해서 삭제할 수 있다")
    void delete() {

    }
}