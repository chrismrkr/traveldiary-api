package kko.traveldiary_api.journey.application;

import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.request.JourneyPatchReqDto;
import kko.traveldiary_api.journey.adaptor.infrastructure.CityVisitJpaRepository;
import kko.traveldiary_api.journey.adaptor.infrastructure.JourneyJpaRepository;
import kko.traveldiary_api.journey.application.exception.InvalidJourneyDateChangeException;
import kko.traveldiary_api.journey.application.exception.JourneyAccessDeniedException;
import kko.traveldiary_api.journey.application.exception.JourneyNotFoundException;
import kko.traveldiary_api.journey.application.required.CityVisitRepository;
import kko.traveldiary_api.journey.application.required.JourneyRepository;
import kko.traveldiary_api.journey.domain.CityVisit;
import kko.traveldiary_api.journey.domain.Journey;
import kko.traveldiary_api.journey.domain.Visibility;
import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.request.JourneyRegisterReqDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class JourneyServiceTest {

    private static final LocalDate START = LocalDate.of(2026, 1, 1);
    private static final LocalDate END = LocalDate.of(2026, 1, 10);

    @Autowired
    JourneyService journeyService;

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

    private Journey saveJourney(Long memberId, String name) {
        return journeyRepository.save(Journey.create(memberId, name, START, END, Visibility.PUBLIC));
    }

    private void addCityVisit(Journey journey, LocalDate startDate, LocalDate endDate) {
        CityVisit cityVisit = CityVisit.builder()
                .cityId(100L)
                .startDate(startDate)
                .endDate(endDate)
                .build();
        journey.visit(cityVisit); // 부모-자식 연결 + journey 역참조 설정
        cityVisitRepository.save(cityVisit);
    }

    @Test
    @DisplayName("Journey 목록을 MemberId로 조회할 수 있다.")
    void findMyJourneys() {
        saveJourney(1L, "도쿄 여행");
        saveJourney(1L, "오사카 여행");
        saveJourney(2L, "파리 여행"); // 다른 회원 → 제외되어야 한다.

        List<Journey> result = journeyService.findMyJourneys(1L);

        assertThat(result).hasSize(2)
                .extracting(Journey::getName)
                .containsExactlyInAnyOrder("도쿄 여행", "오사카 여행");
    }

    @Test
    @DisplayName("Journey를 Id로 조회할 수 있다")
    void findJourney() {
        Long memberId = 1L;
        Journey saved = saveJourney(memberId, "도쿄 여행");

        Journey found = journeyService.findJourney(memberId, saved.getId());

        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getName()).isEqualTo("도쿄 여행");
        assertThat(found.getMemberId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("존재하지 않는 JourneyId로 조회하면 예외가 발생한다")
    void findJourney_notFound() {
        Long memberId = 1L;
        Journey saved = saveJourney(memberId, "도쿄 여행");

        assertThatThrownBy(() -> journeyService.findJourney(memberId, 99_999L))
                .isInstanceOf(JourneyNotFoundException.class);
    }

    @Test
    @DisplayName("소유권이 없는 MemberId로 조회하면 예외가 발생한다")
    void findJourney_InvalidMemberId() {
        Long memberId = 1L;
        Journey saved = saveJourney(memberId, "도쿄 여행");

        assertThatThrownBy(() -> journeyService.findJourney(2L, saved.getId()))
                .isInstanceOf(JourneyAccessDeniedException.class);
    }

    @Test
    @DisplayName("Journey를 등록할 수 있다")
    void register() {
        Long memberId = 1L;
        JourneyRegisterReqDto dto = new JourneyRegisterReqDto(memberId, START, END, "도쿄 여행", "PUBLIC");

        Journey result = journeyService.register(dto);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getMemberId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("도쿄 여행");
        assertThat(result.getStartDate()).isEqualTo(START);
        assertThat(result.getEndDate()).isEqualTo(END);
        assertThat(result.getVisibility()).isEqualTo(Visibility.PUBLIC);
        assertThat(result.getIsActive()).isTrue();
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getLastModifiedAt()).isNotNull();

        // 실제로 영속되었는지 재조회로 확인한다.
        Journey reloaded = journeyService.findJourney(memberId, result.getId());
        assertThat(reloaded.getName()).isEqualTo("도쿄 여행");
    }

    @Test
    @DisplayName("잘못된 Visibility 문자열로 등록하면 예외가 발생한다")
    void register_invalidVisibility() {
        JourneyRegisterReqDto dto = new JourneyRegisterReqDto(1L, START, END, "이상한 여행", "UNKNOWN");

        assertThatThrownBy(() -> journeyService.register(dto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Journey의 시작 및 종료일자를 변경할 수 있다.")
    void modifyJourneyDate() {
        Long memberId = 1L;
        Journey journey = saveJourney(memberId, "도쿄 여행");
        addCityVisit(journey, LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 7));

        Journey result = journeyService.modify(
                memberId,
                new JourneyPatchReqDto(
                journey.getId(), LocalDate.of(2026, 1, 2), LocalDate.of(2026, 1, 9), null, null));

        assertThat(result.getStartDate()).isEqualTo(LocalDate.of(2026, 1, 2));
        assertThat(result.getEndDate()).isEqualTo(LocalDate.of(2026, 1, 9));

        Journey reloaded = journeyService.findJourney(memberId, journey.getId());
        assertThat(reloaded.getStartDate()).isEqualTo(LocalDate.of(2026, 1, 2));
        assertThat(reloaded.getEndDate()).isEqualTo(LocalDate.of(2026, 1, 9));
    }

    @Test
    @DisplayName("Journey 시작일이 도시 방문 시작일 보다 느리게 변경할 수 없다.")
    void modifyJourneyDateFailed() {
        Long memberId = 1L;
        Journey journey = saveJourney(memberId, "도쿄 여행");
        addCityVisit(journey, LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 7));

        // 새 시작일(1/5) 이 도시 방문 시작일(1/3) 보다 늦다 → 거부
        assertThatThrownBy(() -> journeyService.modify(
                memberId,
                new JourneyPatchReqDto(journey.getId(), LocalDate.of(2026, 1, 5), END, null, null)
                ))
                .isInstanceOf(InvalidJourneyDateChangeException.class);

        // 거부되었으므로 저장되지 않아 기존 값이 유지된다.
        Journey reloaded = journeyService.findJourney(memberId, journey.getId());
        assertThat(reloaded.getStartDate()).isEqualTo(START);
    }

    @Test
    @DisplayName("Journey 종료일이 도시 방문 종료일 보다 빠르게 변경할 수 없다.")
    void modifyJourneyDateFailed2() {
        Long memberId = 1L;
        Journey journey = saveJourney(memberId, "도쿄 여행");
        addCityVisit(journey, LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 8));

        // 새 종료일(1/7) 이 도시 방문 종료일(1/8) 보다 빠르다 → 거부
        assertThatThrownBy(() -> journeyService.modify(
                memberId,
                new JourneyPatchReqDto(
                journey.getId(), LocalDate.of(2026, 1, 2), LocalDate.of(2026, 1, 7), null, null)
                ))
                .isInstanceOf(InvalidJourneyDateChangeException.class);

        // 거부되었으므로 저장되지 않아 기존 값이 유지된다.
        Journey reloaded = journeyService.findJourney(memberId, journey.getId());
        assertThat(reloaded.getEndDate()).isEqualTo(END);
    }

    @Test
    @DisplayName("Journey의 공개범위(Visibility)를 변경할 수 있다")
    void changeVisibility() {
        Long memberId = 1L;
        Journey journey = saveJourney(memberId, "도쿄 여행"); // PUBLIC 으로 생성

        Journey aPrivate = journeyService.modify(
                memberId,
                new JourneyPatchReqDto(
                        journey.getId(), null, null, null, "PRIVATE")
        );

        Journey reloaded = journeyService.findJourney(memberId, journey.getId());
        assertThat(reloaded.getVisibility()).isEqualTo(Visibility.PRIVATE);
    }

    @Test
    @DisplayName("Journey의 이름을 변경할 수 있다")
    void modifyName() {
        Long memberId = 1L;
        Journey journey = saveJourney(memberId, "도쿄 여행");

        journeyService.modify(
                memberId,
                new JourneyPatchReqDto(journey.getId(), null, null, "오사카 여행", null));

        Journey reloaded = journeyService.findJourney(memberId, journey.getId());
        assertThat(reloaded.getName()).isEqualTo("오사카 여행");
    }

    @Test
    @DisplayName("존재하지 않는 Journey를 수정하면 예외가 발생한다")
    void modify_notFound() {
        Long memberId = 1L;
        Journey journey = saveJourney(memberId, "도쿄 여행");

        assertThatThrownBy(() -> journeyService.modify(
                memberId,
                new JourneyPatchReqDto(99_999L, START, END, null, null)))
                .isInstanceOf(JourneyNotFoundException.class);
    }

    @Test
    @DisplayName("소유권이 없는 Member가 Journey를 수정하면 예외가 발생한다")
    void modify_notOwn() {
        Long memberId = 1L;
        Journey journey = saveJourney(memberId, "도쿄 여행");

        assertThatThrownBy(() -> journeyService.modify(
                2L,
                new JourneyPatchReqDto(journey.getId(), START, END, null, null)))
                .isInstanceOf(JourneyAccessDeniedException.class);
    }

    @Test
    @DisplayName("Journey를 삭제하면 연관된 CityVisit도 함께 삭제된다")
    void delete() {
        Long memberId = 1L;
        Journey journey = saveJourney(memberId, "도쿄 여행");
        addCityVisit(journey, LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 7));

        journeyService.delete(memberId, journey.getId());

        // Journey 가 제거되고, cascade REMOVE 로 자식 CityVisit 도 함께 사라진다(FK 위반 없이 삭제됨).
        assertThat(journeyRepository.findById(journey.getId())).isEmpty();
        assertThat(cityVisitJpaRepository.count()).isZero();
    }
}
