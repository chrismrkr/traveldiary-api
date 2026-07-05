package kko.traveldiary_api.journey.adaptor.infrastructure;

import kko.traveldiary_api.journey.domain.CityVisit;
import kko.traveldiary_api.journey.domain.Journey;
import kko.traveldiary_api.journey.domain.Visibility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JourneyDatabaseRepository.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
햐record JourneyDatabaseRepositoryTest(JourneyDatabaseRepository repository, TestEntityManager em, JourneyJpaRepository journeyJpaRepository, CityVisitJpaRepository cityVisitJpaRepository) {

    private Journey sampleJourney(Long memberId, String name) {
        return Journey.create(memberId, name,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 10), Visibility.PUBLIC);
    }

    private CityVisit cityVisit(Long cityId) {
        return CityVisit.builder()
                .cityId(cityId)
                .startDate(LocalDate.of(2026, 1, 2))
                .endDate(LocalDate.of(2026, 1, 4))
                .build();
    }

    @BeforeEach
    void init() {
        journeyJpaRepository.deleteAll();
        cityVisitJpaRepository.deleteAll();
    }


    @Test
    @DisplayName("Journey를 저장하면 id가 부여된 도메인으로 반환된다")
    void save() {
        Journey saved = repository.save(sampleJourney(1L, "도쿄 여행"));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getMemberId()).isEqualTo(1L);
        assertThat(saved.getName()).isEqualTo("도쿄 여행");
        assertThat(saved.getVisibility()).isEqualTo(Visibility.PUBLIC);
        assertThat(saved.getIsActive()).isTrue();
    }

    @Test
    @DisplayName("id로 저장된 Journey를 조회한다")
    void findById() {
        Journey saved = repository.save(sampleJourney(1L, "도쿄 여행"));
        em.flush();
        em.clear();

        Optional<Journey> found = repository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("도쿄 여행");
    }

    @Test
    @DisplayName("존재하지 않는 id로 조회하면 비어있다")
    void findById_notFound() {
        assertThat(repository.findById(999L)).isEmpty();
    }

    @Test
    @DisplayName("memberId로 해당 회원의 Journey들만 조회한다")
    void findByMemberId() {
        repository.save(sampleJourney(1L, "도쿄 여행"));
        repository.save(sampleJourney(1L, "오사카 여행"));
        repository.save(sampleJourney(2L, "파리 여행"));

        List<Journey> journeys = repository.findByMemberId(1L);

        assertThat(journeys).hasSize(2)
                .extracting(Journey::getName)
                .containsExactlyInAnyOrder("도쿄 여행", "오사카 여행");
    }

    @Test
    @DisplayName("CityVisit을 저장하면 기존 Journey에 FK로 연결되어 영속된다")
    void saveCityVisit() {
        Journey journey = repository.save(sampleJourney(1L, "도쿄 여행"));

        CityVisit cityVisit = cityVisit(100L);
        journey.visit(cityVisit); // 부모-자식 연결 + journey 역참조 설정

        CityVisit saved = repository.save(cityVisit);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCityId()).isEqualTo(100L);
        assertThat(saved.getJourney().getId()).isEqualTo(journey.getId());
    }

    @Test
    @DisplayName("findByIdWithCityVisit은 Journey와 연관된 CityVisit을 함께 조회한다")
    void findByIdWithCityVisit() {
        Journey journey = repository.save(sampleJourney(1L, "도쿄 여행"));
        for (long cityId = 100L; cityId < 103L; cityId++) {
            CityVisit visit = cityVisit(cityId);
            journey.visit(visit);
            repository.save(visit);
        }
        // 영속성 컨텍스트를 비워, fetch join 쿼리가 실제 DB에서 자식을 로딩하는지 검증한다.
        em.flush();
        em.clear();

        Optional<Journey> found = repository.findByIdWithCityVisit(journey.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getCityVisitList()).hasSize(3)
                .extracting(CityVisit::getCityId)
                .containsExactlyInAnyOrder(100L, 101L, 102L);
        // 자식 -> 부모 역참조는 동일한 journey id 를 가리킨다.
        assertThat(found.get().getCityVisitList())
                .allSatisfy(v -> assertThat(v.getJourney().getId()).isEqualTo(journey.getId()));
    }

    @Test
    @DisplayName("CityVisit을 Journey와 함께 조회할 수 있다.")
    void findCityVisitByIdWithJourney() {
        // given
        Journey journey = repository.save(sampleJourney(1L, "도쿄 여행"));
        CityVisit cityVisit = cityVisit(100L);
        journey.visit(cityVisit);
        cityVisit = repository.save(cityVisit);
        em.flush();
        em.clear();

        // when
        Optional<CityVisit> found = repository.findCityVisitByIdWithJourney(cityVisit.getId());

        // then
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals(found.get().getJourney().getId(), journey.getId());
        Assertions.assertEquals(found.get().getId(), cityVisit.getId());
        Assertions.assertEquals(found.get().getCityId(), cityVisit.getCityId());

    }
}
