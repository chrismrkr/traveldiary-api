package kko.traveldiary_api.journey.adaptor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.request.JourneyPatchReqDto;
import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.request.JourneyRegisterReqDto;
import kko.traveldiary_api.journey.adaptor.infrastructure.CityVisitJpaRepository;
import kko.traveldiary_api.journey.adaptor.infrastructure.JourneyJpaRepository;
import kko.traveldiary_api.journey.application.required.CityVisitRepository;
import kko.traveldiary_api.journey.application.required.JourneyRepository;
import kko.traveldiary_api.journey.domain.CityVisit;
import kko.traveldiary_api.journey.domain.Journey;
import kko.traveldiary_api.journey.domain.Visibility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class JourneyControllerTest {

    private static final LocalDate START = LocalDate.of(2026, 1, 1);
    private static final LocalDate END = LocalDate.of(2026, 1, 10);
    private static final Long OWNER = 1L;
    private static final Long OTHER = 2L;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

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
                .cityId(100L).cityName("Tokyo").startDate(startDate).endDate(endDate).build();
        journey.visit(cityVisit);
        cityVisitRepository.save(cityVisit);
    }

    /** access token 대신 subject(=memberId) 를 담은 JWT principal 을 주입한다. */
    private static org.springframework.test.web.servlet.request.RequestPostProcessor accessToken(Long memberId) {
        return jwt().jwt(builder -> builder.subject(String.valueOf(memberId)));
    }

    @Test
    @DisplayName("POST /api/journey - Journey를 등록하면 200과 등록된 Journey를 반환한다")
    void register() throws Exception {
        JourneyRegisterReqDto dto = new JourneyRegisterReqDto(START, END, "도쿄 여행", "PUBLIC");

        mockMvc.perform(post("/api/journey")
                        .with(accessToken(OWNER))
                        .header("Idempotency-Key", java.util.UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.name").value("도쿄 여행"))
                .andExpect(jsonPath("$.data.journeyId").isNumber());

        assertThat(journeyRepository.findByMemberId(OWNER)).hasSize(1);
    }

    @Test
    @DisplayName("GET /api/journey/{id} - 소유자가 조회하면 200과 Journey를 반환한다")
    void findJourney() throws Exception {
        Journey journey = saveJourney(OWNER, "도쿄 여행");

        mockMvc.perform(get("/api/journey/{journeyId}", journey.getId())
                        .with(accessToken(OWNER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.journeyId").value(journey.getId()))
                .andExpect(jsonPath("$.data.name").value("도쿄 여행"));
    }

    @Test
    @DisplayName("GET /api/journey - 내 여행 목록만 반환한다")
    void findMyJourneys() throws Exception {
        saveJourney(OWNER, "도쿄 여행");
        saveJourney(OWNER, "오사카 여행");
        saveJourney(OTHER, "남의 여행"); // 제외되어야 한다

        mockMvc.perform(get("/api/journey")
                        .with(accessToken(OWNER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/journey - 각 여행의 CityVisit을 함께 반환한다")
    void findMyJourneys_withCityVisits() throws Exception {
        Journey journey = saveJourney(OWNER, "도쿄 여행");
        addCityVisit(journey, LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 7));
        addCityVisit(journey, LocalDate.of(2026, 1, 7), LocalDate.of(2026, 1, 9));

        mockMvc.perform(get("/api/journey")
                        .with(accessToken(OWNER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                // CityVisit 이 2건이어도 Journey 는 중복 없이 1건이어야 한다.
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].name").value("도쿄 여행"))
                .andExpect(jsonPath("$.data[0].cityVisits.length()").value(2))
                .andExpect(jsonPath("$.data[0].cityVisits[0].cityName").value("Tokyo"));
    }

    @Test
    @DisplayName("GET /api/journey - CityVisit이 없으면 빈 배열을 반환한다")
    void findMyJourneys_withoutCityVisits() throws Exception {
        saveJourney(OWNER, "도쿄 여행");

        mockMvc.perform(get("/api/journey")
                        .with(accessToken(OWNER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].cityVisits").isArray())
                .andExpect(jsonPath("$.data[0].cityVisits").isEmpty());
    }

    @Test
    @DisplayName("GET /api/journey/{id} - 존재하지 않는 Journey면 404")
    void findJourney_notFound() throws Exception {
        mockMvc.perform(get("/api/journey/{journeyId}", 99_999L)
                        .with(accessToken(OWNER)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/journey/{id} - 소유자가 아니면 403")
    void findJourney_accessDenied() throws Exception {
        Journey journey = saveJourney(OWNER, "도쿄 여행");

        mockMvc.perform(get("/api/journey/{journeyId}", journey.getId())
                        .with(accessToken(OTHER)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value("NOT_OWNED_JOURNEY_ACCESS"));
    }

    @Test
    @DisplayName("PATCH /api/journey - 이름을 변경하면 200과 변경된 Journey를 반환한다")
    void modifyName() throws Exception {
        Journey journey = saveJourney(OWNER, "도쿄 여행");
        JourneyPatchReqDto patch = new JourneyPatchReqDto(journey.getId(), null, null, "오사카 여행", null);

        mockMvc.perform(patch("/api/journey")
                        .with(accessToken(OWNER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.name").value("오사카 여행"));

        assertThat(journeyRepository.findById(journey.getId()).orElseThrow().getName())
                .isEqualTo("오사카 여행");
    }

    @Test
    @DisplayName("PATCH /api/journey - 도시 방문 기간을 벗어난 날짜 변경은 400")
    void modifyDate_invalid() throws Exception {
        Journey journey = saveJourney(OWNER, "도쿄 여행");
        addCityVisit(journey, LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 7));

        // 새 시작일(1/5)이 도시 방문 시작일(1/3)보다 늦음 → 거부
        JourneyPatchReqDto patch = new JourneyPatchReqDto(
                journey.getId(), LocalDate.of(2026, 1, 5), END, null, null);

        mockMvc.perform(patch("/api/journey")
                        .with(accessToken(OWNER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("INVALID_JOURNEY_DATE_CHANGE"));
    }

    @Test
    @DisplayName("PATCH /api/journey - 소유자가 아니면 403")
    void modify_accessDenied() throws Exception {
        Journey journey = saveJourney(OWNER, "도쿄 여행");
        JourneyPatchReqDto patch = new JourneyPatchReqDto(journey.getId(), null, null, "오사카 여행", null);

        mockMvc.perform(patch("/api/journey")
                        .with(accessToken(OTHER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value("NOT_OWNED_JOURNEY_ACCESS"));

        // 거부되었으므로 이름이 그대로여야 한다.
        assertThat(journeyRepository.findById(journey.getId()).orElseThrow().getName())
                .isEqualTo("도쿄 여행");
    }

    @Test
    @DisplayName("DELETE /api/journey/{id} - 소유자가 삭제하면 204")
    void delete_success() throws Exception {
        Journey journey = saveJourney(OWNER, "도쿄 여행");

        mockMvc.perform(delete("/api/journey/{journeyId}", journey.getId())
                        .with(accessToken(OWNER)))
                .andExpect(status().isNoContent());

        assertThat(journeyRepository.findById(journey.getId())).isEmpty();
    }

    @Test
    @DisplayName("DELETE /api/journey/{id} - 소유자가 아니면 403이고 삭제되지 않는다")
    void delete_accessDenied() throws Exception {
        Journey journey = saveJourney(OWNER, "도쿄 여행");

        mockMvc.perform(delete("/api/journey/{journeyId}", journey.getId())
                        .with(accessToken(OTHER)))
                .andExpect(status().isForbidden());

        assertThat(journeyRepository.findById(journey.getId())).isPresent();
    }
}
