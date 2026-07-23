package kko.traveldiary_api.journey.application;

import kko.traveldiary_api.journey.adaptor.infrastructure.CityVisitJpaRepository;
import kko.traveldiary_api.journey.adaptor.infrastructure.JourneyJpaRepository;
import kko.traveldiary_api.journey.application.exception.CityVisitNotFoundException;
import kko.traveldiary_api.journey.application.exception.JourneyAccessDeniedException;
import kko.traveldiary_api.journey.application.exception.JourneyNotFoundException;
import kko.traveldiary_api.journey.application.required.CityQueryPort;
import kko.traveldiary_api.journey.application.required.CityVisitRepository;
import kko.traveldiary_api.journey.application.required.JourneyRepository;
import kko.traveldiary_api.journey.domain.CityVisit;
import kko.traveldiary_api.journey.domain.Journey;
import kko.traveldiary_api.journey.domain.Visibility;
import kko.traveldiary_api.shared.Coordinate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class CityVisitServiceTest {

    private static final LocalDate START = LocalDate.of(2026, 1, 1);
    private static final LocalDate END = LocalDate.of(2026, 1, 10);
    private static final Coordinate COORDINATE = new Coordinate(35.6762, 139.6503); // 도쿄

    @MockitoBean
    CityQueryPort cityQueryPort;

    @Autowired
    CityVisitService cityVisitService;

    @Autowired
    JourneyRepository journeyRepository;

    @Autowired
    CityVisitRepository cityVisitRepository;

    @Autowired
    JourneyJpaRepository journeyJpaRepository;

    @Autowired
    CityVisitJpaRepository cityVisitJpaRepository;

    @BeforeEach
    @AfterEach
    void clean() {
        // FK(city_visit.journey_id) 때문에 자식부터 삭제한다.
        cityVisitJpaRepository.deleteAll();
        journeyJpaRepository.deleteAll();
    }

    private Journey saveJourney(Long memberId) {
        return journeyRepository.save(Journey.create(memberId, "도쿄 여행", START, END, Visibility.PUBLIC));
    }

    private CityVisit saveCityVisit(Journey journey, LocalDate startDate, LocalDate endDate) {
        CityVisit cityVisit = CityVisit.builder()
                .cityId(100L)
                .startDate(startDate)
                .endDate(endDate)
                .build();
        journey.visit(cityVisit); // 부모-자식 연결 + journey 역참조 설정
        return cityVisitRepository.save(cityVisit);
    }

    @Test
    @DisplayName("도시 방문 이력을 Journey에 추가할 수 있다.")
    void visit() {
        Long memberId = 1L;
        Journey journey = saveJourney(1L);
        given(cityQueryPort.search(any(), any(), any()))
                .willReturn(new CityQueryPort.CityInfo(100L));

        CityVisit result = cityVisitService.visit(memberId,
                journey.getId(), "Tokyo", "place-tokyo", COORDINATE,
                LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 7));

        assertThat(result.getId()).isNotNull();
        assertThat(result.getCityId()).isEqualTo(100L); // CityQueryPort 가 해석한 cityId
        assertThat(result.getStartDate()).isEqualTo(LocalDate.of(2026, 1, 3));
        assertThat(result.getEndDate()).isEqualTo(LocalDate.of(2026, 1, 7));
        assertThat(result.getJourneyId()).isEqualTo(journey.getId());

        // 실제 영속 확인
        List<CityVisit> cityVisits = journeyRepository.findByIdWithCityVisit(journey.getId()).orElseThrow().getCityVisits();
        assertThat(cityVisits.size()).isGreaterThan(0);

    }

    @Test
    @DisplayName("도시 방문 이력을 Journey에 추가할 수 있지만 start-endDate가 Journey 범위를 벗어나면 실패한다")
    void visitFail() {
        Long memberId = 1L;
        Journey journey = saveJourney(memberId);
        given(cityQueryPort.search(any(), any(), any()))
                .willReturn(new CityQueryPort.CityInfo(100L));

        // 방문 종료일(1/11)이 Journey 종료일(1/10)을 벗어남 → 거부
        assertThatThrownBy(() -> cityVisitService.visit(memberId,
                journey.getId(), "Tokyo", "place-tokyo", COORDINATE,
                LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 11)))
                .isInstanceOf(IllegalArgumentException.class);

        // 저장되지 않았어야 한다.
        assertThat(cityVisitJpaRepository.count()).isZero();
    }

    @Test
    @DisplayName("존재하지 않는 Journey에 방문을 추가하면 예외가 발생한다")
    void visit_journeyNotFound() {
        Long memberId = 1L;
        Journey journey = saveJourney(memberId);

        assertThatThrownBy(() -> cityVisitService.visit(memberId,
                99_999L, "Tokyo", "place-tokyo", COORDINATE,
                LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 7)))
                .isInstanceOf(JourneyNotFoundException.class);
    }

    @Test
    @DisplayName("잘못된 Member가 Journey에 방문을 추가하면 예외가 발생한다")
    void visit_journeyAccessDenied() {
        Long memberId = 1L;
        Journey journey = saveJourney(memberId);

        assertThatThrownBy(() -> cityVisitService.visit(99_99L,
                journey.getId(), "Tokyo", "place-tokyo", COORDINATE,
                LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 7)))
                .isInstanceOf(JourneyAccessDeniedException.class);
    }



    @Test
    @DisplayName("도시 방문 기간을 변경할 수 있다")
    void changeDate() {
        Long memberId = 1L;
        Journey journey = saveJourney(memberId);
        CityVisit cityVisit = saveCityVisit(journey, LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 7));

        CityVisit result = cityVisitService.changeDate(memberId, journey.getId(),
                cityVisit.getId(), LocalDate.of(2026, 1, 4), LocalDate.of(2026, 1, 6));

        assertThat(result.getStartDate()).isEqualTo(LocalDate.of(2026, 1, 4));
        assertThat(result.getEndDate()).isEqualTo(LocalDate.of(2026, 1, 6));

        CityVisit reloaded = cityVisitRepository.findCityVisitByIdWithJourney(cityVisit.getId()).orElseThrow();
        assertThat(reloaded.getStartDate()).isEqualTo(LocalDate.of(2026, 1, 4));
        assertThat(reloaded.getEndDate()).isEqualTo(LocalDate.of(2026, 1, 6));
    }

    @Test
    @DisplayName("방문 시작일이 여행 시작일보다 이르면 변경할 수 없다")
    void changeDate_startBeforeJourneyStart() {
        Long memberId = 1L;
        Journey journey = saveJourney(memberId);
        CityVisit cityVisit = saveCityVisit(journey, LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 7));

        // 여행 시작일(1/1) 이전인 2025-12-31 로 변경 시도 → 거부
        assertThatThrownBy(() -> cityVisitService.changeDate(memberId, journey.getId(),
                cityVisit.getId(), LocalDate.of(2025, 12, 31), LocalDate.of(2026, 1, 7)))
                .isInstanceOf(IllegalArgumentException.class);

        CityVisit reloaded = cityVisitRepository.findCityVisitByIdWithJourney(cityVisit.getId()).orElseThrow();
        assertThat(reloaded.getStartDate()).isEqualTo(LocalDate.of(2026, 1, 3)); // 미변경
    }

    @Test
    @DisplayName("방문 종료일이 여행 종료일보다 늦으면 변경할 수 없다")
    void changeDate_endAfterJourneyEnd() {
        Long memberId = 1L;
        Journey journey = saveJourney(memberId);
        CityVisit cityVisit = saveCityVisit(journey, LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 7));

        // 여행 종료일(1/10) 이후인 1/11 로 변경 시도 → 거부
        assertThatThrownBy(() -> cityVisitService.changeDate(memberId, journey.getId(),
                cityVisit.getId(), LocalDate.of(2026, 1, 4), LocalDate.of(2026, 1, 11)))
                .isInstanceOf(IllegalArgumentException.class);

        CityVisit reloaded = cityVisitRepository.findCityVisitByIdWithJourney(cityVisit.getId()).orElseThrow();
        assertThat(reloaded.getEndDate()).isEqualTo(LocalDate.of(2026, 1, 7)); // 미변경
    }

    @Test
    @DisplayName("잘못된 Member가 CityVisit의 기간을 변경하면 예외가 발생한다")
    void changeDate_accessDenied() {
        Long memberId = 1L;
        Journey journey = saveJourney(memberId);
        CityVisit cityVisit = saveCityVisit(journey, LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 7));

        assertThatThrownBy(() -> cityVisitService.changeDate(10000L, journey.getId(),
                cityVisit.getId(), LocalDate.of(2026, 1, 4), LocalDate.of(2026, 1, 6)))
                .isInstanceOf(JourneyAccessDeniedException.class);
    }

    @Test
    @DisplayName("존재하지 않는 CityVisit의 기간을 변경하면 예외가 발생한다")
    void changeDate_notFound() {
        Long memberId = 1L;
        Journey journey = saveJourney(memberId);
        CityVisit cityVisit = saveCityVisit(journey, LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 7));

        assertThatThrownBy(() -> cityVisitService.changeDate(memberId, journey.getId(),
                99_999L, LocalDate.of(2026, 1, 4), LocalDate.of(2026, 1, 6)))
                .isInstanceOf(CityVisitNotFoundException.class);
    }

    @Test
    @DisplayName("도시 방문 이력을 삭제할 수 있다")
    void delete() {
        Long memberId = 1L;
        Journey journey = saveJourney(memberId);
        CityVisit cityVisit = saveCityVisit(journey, LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 7));

        cityVisitService.delete(memberId, journey.getId(), cityVisit.getId());

        assertThat(cityVisitRepository.findCityVisitByIdWithJourney(cityVisit.getId())).isEmpty();
        // 부모 Journey 는 남아 있어야 한다.
        assertThat(journeyRepository.findById(journey.getId())).isPresent();
    }
}
