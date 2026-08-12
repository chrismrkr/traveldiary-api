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
import static org.assertj.core.api.InstanceOfAssertFactories.list;

@DataJpaTest
@Import(JourneyDatabaseRepository.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
record JourneyDatabaseRepositoryTest(JourneyDatabaseRepository repository, TestEntityManager em, JourneyJpaRepository journeyJpaRepository, CityVisitJpaRepository cityVisitJpaRepository) {

    private Journey sampleJourney(Long memberId, String name) {
        return Journey.create(memberId, name,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 10), Visibility.PUBLIC);
    }

    private CityVisit cityVisit(Long cityId) {
        return CityVisit.builder()
                .cityId(cityId)
                .cityName("Tokyo")
                .startDate(LocalDate.of(2026, 1, 2))
                .endDate(LocalDate.of(2026, 1, 4))
                .build();
    }

    /** journey 에 cityId 들을 방문으로 붙여 영속화한다. */
    private void addCityVisits(Journey journey, long... cityIds) {
        for (long cityId : cityIds) {
            CityVisit visit = cityVisit(cityId);
            journey.visit(visit);
            repository.save(visit);
        }
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
    @DisplayName("findByMemberIdWithCityVisit은 해당 회원의 Journey를 CityVisit과 함께 조회한다")
    void findByMemberIdWithCityVisit() {
        Journey tokyo = repository.save(sampleJourney(1L, "도쿄 여행"));
        addCityVisits(tokyo, 100L, 101L);
        Journey osaka = repository.save(sampleJourney(1L, "오사카 여행"));
        addCityVisits(osaka, 200L);
        Journey paris = repository.save(sampleJourney(2L, "파리 여행")); // 다른 회원 → 제외되어야 한다
        addCityVisits(paris, 300L);
        // 영속성 컨텍스트를 비워, fetch join 쿼리가 실제 DB에서 자식을 로딩하는지 검증한다.
        em.flush();
        em.clear();

        List<Journey> journeys = repository.findByMemberIdWithCityVisit(1L);

        assertThat(journeys).hasSize(2)
                .extracting(Journey::getName)
                .containsExactlyInAnyOrder("도쿄 여행", "오사카 여행");
        assertThat(journeys)
                .filteredOn(j -> j.getName().equals("도쿄 여행"))
                .singleElement()
                .extracting(Journey::getCityVisits, list(CityVisit.class))
                .extracting(CityVisit::getCityId)
                .containsExactlyInAnyOrder(100L, 101L);
    }

    @Test
    @DisplayName("findByMemberIdWithCityVisit은 CityVisit이 여러 개여도 Journey를 중복 없이 반환한다")
    void findByMemberIdWithCityVisit_noDuplicates() {
        Journey journey = repository.save(sampleJourney(1L, "도쿄 여행"));
        addCityVisits(journey, 100L, 101L, 102L);
        em.flush();
        em.clear();

        List<Journey> journeys = repository.findByMemberIdWithCityVisit(1L);

        // fetch join 은 자식 수만큼 행을 만들지만 Journey 는 한 번만 나와야 한다.
        assertThat(journeys).singleElement()
                .extracting(Journey::getCityVisits, list(CityVisit.class))
                .hasSize(3);
    }

    @Test
    @DisplayName("findByMemberIdWithCityVisit은 CityVisit이 없는 Journey도 반환한다")
    void findByMemberIdWithCityVisit_withoutCityVisit() {
        repository.save(sampleJourney(1L, "도쿄 여행"));
        em.flush();
        em.clear();

        List<Journey> journeys = repository.findByMemberIdWithCityVisit(1L);

        assertThat(journeys).singleElement()
                .extracting(Journey::getCityVisits, list(CityVisit.class))
                .isEmpty();
    }

    @Test
    @DisplayName("findByMemberIdWithCityVisit은 Journey가 없으면 빈 목록을 반환한다")
    void findByMemberIdWithCityVisit_notFound() {
        assertThat(repository.findByMemberIdWithCityVisit(999L)).isEmpty();
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
        assertThat(saved.getJourneyId()).isEqualTo(journey.getId());
    }

    @Test
    @DisplayName("findByIdWithCityVisit은 Journey와 연관된 CityVisit을 함께 조회한다")
    void findByIdWithCityVisit() {
        Journey journey = repository.save(sampleJourney(1L, "도쿄 여행"));
        addCityVisits(journey, 100L, 101L, 102L);
        // 영속성 컨텍스트를 비워, fetch join 쿼리가 실제 DB에서 자식을 로딩하는지 검증한다.
        em.flush();
        em.clear();

        Optional<Journey> found = repository.findByIdWithCityVisit(journey.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getCityVisits()).hasSize(3)
                .extracting(CityVisit::getCityId)
                .containsExactlyInAnyOrder(100L, 101L, 102L);
        // 자식 -> 부모 역참조는 동일한 journey id 를 가리킨다.
        assertThat(found.get().getCityVisits())
                .allSatisfy(v -> assertThat(v.getJourneyId()).isEqualTo(journey.getId()));
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
        Assertions.assertEquals(found.get().getJourneyId(), journey.getId());
        Assertions.assertEquals(found.get().getId(), cityVisit.getId());
        Assertions.assertEquals(found.get().getCityId(), cityVisit.getCityId());

    }
}
