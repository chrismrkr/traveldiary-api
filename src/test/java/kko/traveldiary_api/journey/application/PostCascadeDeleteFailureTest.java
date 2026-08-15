package kko.traveldiary_api.journey.application;

import kko.traveldiary_api.journey.adaptor.infrastructure.CityVisitJpaRepository;
import kko.traveldiary_api.journey.adaptor.infrastructure.JourneyJpaRepository;
import kko.traveldiary_api.journey.application.required.CityVisitRepository;
import kko.traveldiary_api.journey.application.required.JourneyRepository;
import kko.traveldiary_api.journey.application.exception.JourneyAccessDeniedException;
import kko.traveldiary_api.journey.application.required.PostQueryPort;
import kko.traveldiary_api.journey.domain.CityVisit;
import kko.traveldiary_api.journey.domain.Journey;
import kko.traveldiary_api.journey.domain.Visibility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

/**
 * Post 삭제가 실패했을 때 Journey/CityVisit 삭제가 중단되는지 확인한다.
 * 보상 트랜잭션은 두지 않기로 했으므로, "실패하면 아무것도 지우지 않고 멈춘다 → 다시 삭제하면 된다" 가
 * 성립하는지가 핵심이다.
 */
@SpringBootTest
class PostCascadeDeleteFailureTest {

    private static final LocalDate START = LocalDate.of(2026, 1, 1);
    private static final LocalDate END = LocalDate.of(2026, 1, 10);
    private static final Long OWNER = 1L;

    @MockitoBean
    PostQueryPort postQueryPort;

    @Autowired
    JourneyService journeyService;

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
        cityVisitJpaRepository.deleteAll();
        journeyJpaRepository.deleteAll();
    }

    private Journey saveJourney() {
        return journeyRepository.save(Journey.create(OWNER, "도쿄 여행", START, END, Visibility.PUBLIC));
    }

    private CityVisit saveCityVisit(Journey journey, LocalDate startDate, LocalDate endDate) {
        CityVisit cityVisit = CityVisit.builder()
                .cityId(100L).cityName("Tokyo").startDate(startDate).endDate(endDate).build();
        journey.visit(cityVisit);
        return cityVisitRepository.save(cityVisit);
    }

    @Test
    @DisplayName("Post 삭제에 실패하면 Journey 는 삭제되지 않는다")
    void journeyDelete_abortsWhenPostDetachFails() {
        Journey journey = saveJourney();
        saveCityVisit(journey, LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 7));
        given(postQueryPort.detachByCityVisitId(anyLong())).willReturn(false);

        assertThatThrownBy(() -> journeyService.delete(OWNER, journey.getId()))
                .isInstanceOf(IllegalStateException.class);

        // 다시 삭제를 시도할 수 있도록 Journey 와 CityVisit 이 남아 있어야 한다.
        assertThat(journeyRepository.findById(journey.getId())).isPresent();
        assertThat(cityVisitJpaRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("CityVisit 여러 개 중 하나라도 Post 삭제에 실패하면 Journey 는 삭제되지 않는다")
    void journeyDelete_abortsWhenAnyPostDetachFails() {
        Journey journey = saveJourney();
        CityVisit first = saveCityVisit(journey, LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 4));
        CityVisit second = saveCityVisit(journey, LocalDate.of(2026, 1, 5), LocalDate.of(2026, 1, 6));
        given(postQueryPort.detachByCityVisitId(first.getId())).willReturn(true);
        given(postQueryPort.detachByCityVisitId(second.getId())).willReturn(false);

        assertThatThrownBy(() -> journeyService.delete(OWNER, journey.getId()))
                .isInstanceOf(IllegalStateException.class);

        assertThat(journeyRepository.findById(journey.getId())).isPresent();
    }

    @Test
    @DisplayName("모든 CityVisit 의 Post 삭제에 성공하면 Journey 가 삭제된다")
    void journeyDelete_succeedsWhenAllPostDetachSucceed() {
        Journey journey = saveJourney();
        CityVisit first = saveCityVisit(journey, LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 4));
        CityVisit second = saveCityVisit(journey, LocalDate.of(2026, 1, 5), LocalDate.of(2026, 1, 6));
        given(postQueryPort.detachByCityVisitId(anyLong())).willReturn(true);

        journeyService.delete(OWNER, journey.getId());

        assertThat(journeyRepository.findById(journey.getId())).isEmpty();
        // 각 CityVisit 마다 정확히 한 번씩 Post 삭제를 요청해야 한다.
        then(postQueryPort).should().detachByCityVisitId(first.getId());
        then(postQueryPort).should().detachByCityVisitId(second.getId());
    }

    @Test
    @DisplayName("Post 삭제에 실패하면 CityVisit 은 삭제되지 않는다")
    void cityVisitDelete_abortsWhenPostDetachFails() {
        Journey journey = saveJourney();
        CityVisit cityVisit = saveCityVisit(journey, LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 7));
        given(postQueryPort.detachByCityVisitId(anyLong())).willReturn(false);

        assertThatThrownBy(() -> cityVisitService.delete(OWNER, journey.getId(), cityVisit.getId()))
                .isInstanceOf(IllegalStateException.class);

        assertThat(cityVisitRepository.findCityVisitByIdWithJourney(cityVisit.getId())).isPresent();
    }

    @Test
    @DisplayName("소유자가 아니면 Post 삭제를 시도조차 하지 않는다")
    void delete_accessDenied_doesNotTouchPosts() {
        Journey journey = saveJourney();
        CityVisit cityVisit = saveCityVisit(journey, LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 7));

        assertThatThrownBy(() -> cityVisitService.delete(2L, journey.getId(), cityVisit.getId()))
                .isInstanceOf(JourneyAccessDeniedException.class);
        assertThatThrownBy(() -> journeyService.delete(2L, journey.getId()))
                .isInstanceOf(JourneyAccessDeniedException.class);

        then(postQueryPort).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("CityVisit 이 없는 Journey 는 Post 삭제 없이 바로 삭제된다")
    void journeyDelete_withoutCityVisit() {
        Journey journey = saveJourney();

        journeyService.delete(OWNER, journey.getId());

        assertThat(journeyRepository.findById(journey.getId())).isEmpty();
        then(postQueryPort).shouldHaveNoInteractions();
    }
}
