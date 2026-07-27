package kko.traveldiary_api.journey.adaptor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.request.CityVisitCreateReqDto;
import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.request.CityVisitModifyReqDto;
import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.request.CityVisitOrderRealignReqDto;
import kko.traveldiary_api.journey.adaptor.infrastructure.CityVisitJpaRepository;
import kko.traveldiary_api.journey.adaptor.infrastructure.JourneyJpaRepository;
import kko.traveldiary_api.journey.application.required.CityQueryPort;
import kko.traveldiary_api.journey.application.required.CityVisitRepository;
import kko.traveldiary_api.journey.application.required.JourneyRepository;
import kko.traveldiary_api.journey.domain.CityVisit;
import kko.traveldiary_api.journey.domain.CityVisitOrder;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CityVisitControllerTest {

    private static final LocalDate START = LocalDate.of(2026, 1, 1);
    private static final LocalDate END = LocalDate.of(2026, 1, 10);
    private static final Long OWNER = 1L;
    private static final Long OTHER = 2L;

    @MockitoBean
    CityQueryPort cityQueryPort;

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
        cityVisitJpaRepository.deleteAll();
        journeyJpaRepository.deleteAll();
    }

    private static RequestPostProcessor accessToken(Long memberId) {
        return jwt().jwt(builder -> builder.subject(String.valueOf(memberId)));
    }

    private Journey saveJourney(Long memberId) {
        return journeyRepository.save(Journey.create(memberId, "도쿄 여행", START, END, Visibility.PUBLIC));
    }

    private CityVisit saveCityVisit(Journey journey, LocalDate startDate, LocalDate endDate) {
        CityVisit cityVisit = CityVisit.builder()
                .cityId(100L).startDate(startDate).endDate(endDate).build();
        journey.visit(cityVisit);
        return cityVisitRepository.save(cityVisit);
    }

    private int reloadVisitOrder(Long cityVisitId) {
        return cityVisitRepository.findCityVisitByIdWithJourney(cityVisitId).orElseThrow().getVisitOrder();
    }

    private String json(Object body) throws Exception {
        return objectMapper.writeValueAsString(body);
    }

    private CityVisitCreateReqDto createReq(Long journeyId, LocalDate startDate, LocalDate endDate) {
        return new CityVisitCreateReqDto(journeyId, "Tokyo", "place-tokyo",
                new BigDecimal("35.6762"), new BigDecimal("139.6503"), startDate, endDate);
    }

    @Test
    @DisplayName("POST /api/city-visit - 방문을 등록하면 200과 SUCCESS 를 반환하고 영속된다")
    void register() throws Exception {
        given(cityQueryPort.search(any(), any(), any())).willReturn(new CityQueryPort.CityInfo(100L));
        Journey journey = saveJourney(OWNER);

        mockMvc.perform(post("/api/city-visit")
                        .with(accessToken(OWNER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(createReq(journey.getId(), LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 7)))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        assertThat(cityVisitJpaRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("POST /api/city-visit - 소유자가 아니면 403")
    void register_accessDenied() throws Exception {
        Journey journey = saveJourney(OWNER);

        mockMvc.perform(post("/api/city-visit")
                        .with(accessToken(OTHER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(createReq(journey.getId(), LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 7)))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value("NOT_OWNED_JOURNEY_ACCESS"));
    }

    @Test
    @DisplayName("POST /api/city-visit - 방문 종료일이 여행 범위를 벗어나면 400")
    void register_dateInvalid() throws Exception {
        given(cityQueryPort.search(any(), any(), any())).willReturn(new CityQueryPort.CityInfo(100L));
        Journey journey = saveJourney(OWNER);

        // 방문 종료일(1/11)이 여행 종료일(1/10)을 벗어남
        mockMvc.perform(post("/api/city-visit")
                        .with(accessToken(OWNER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(createReq(journey.getId(), LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 11)))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("INVALID_CITY_VISIT_DATE_CHANGE"));
    }

    @Test
    @DisplayName("PATCH /api/city-visit - 방문 기간을 수정하면 200")
    void modify() throws Exception {
        Journey journey = saveJourney(OWNER);
        CityVisit cityVisit = saveCityVisit(journey, LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 7));
        CityVisitModifyReqDto dto = new CityVisitModifyReqDto(
                journey.getId(), cityVisit.getId(), LocalDate.of(2026, 1, 4), LocalDate.of(2026, 1, 6));

        mockMvc.perform(patch("/api/city-visit")
                        .with(accessToken(OWNER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        CityVisit reloaded = cityVisitRepository.findCityVisitByIdWithJourney(cityVisit.getId()).orElseThrow();
        assertThat(reloaded.getStartDate()).isEqualTo(LocalDate.of(2026, 1, 4));
        assertThat(reloaded.getEndDate()).isEqualTo(LocalDate.of(2026, 1, 6));
    }

    @Test
    @DisplayName("PATCH /api/city-visit - 여행 범위를 벗어난 날짜 수정은 400")
    void modify_dateInvalid() throws Exception {
        Journey journey = saveJourney(OWNER);
        CityVisit cityVisit = saveCityVisit(journey, LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 7));
        // 여행 시작일(1/1) 이전인 2025-12-31 로 변경 시도
        CityVisitModifyReqDto dto = new CityVisitModifyReqDto(
                journey.getId(), cityVisit.getId(), LocalDate.of(2025, 12, 31), LocalDate.of(2026, 1, 6));

        mockMvc.perform(patch("/api/city-visit")
                        .with(accessToken(OWNER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("INVALID_CITY_VISIT_DATE_CHANGE"));
    }

    @Test
    @DisplayName("PATCH /api/city-visit - 존재하지 않는 방문을 수정하면 404")
    void modify_notFound() throws Exception {
        Journey journey = saveJourney(OWNER);
        CityVisitModifyReqDto dto = new CityVisitModifyReqDto(
                journey.getId(), 99_999L, LocalDate.of(2026, 1, 4), LocalDate.of(2026, 1, 6));

        mockMvc.perform(patch("/api/city-visit")
                        .with(accessToken(OWNER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /api/city-visit/order - 순서를 재정렬하면 200이고 0..n-1 로 정규화된다")
    void realignOrder() throws Exception {
        Journey journey = saveJourney(OWNER);
        CityVisit a = saveCityVisit(journey, LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 4));
        CityVisit b = saveCityVisit(journey, LocalDate.of(2026, 1, 5), LocalDate.of(2026, 1, 6));
        CityVisit c = saveCityVisit(journey, LocalDate.of(2026, 1, 7), LocalDate.of(2026, 1, 8));

        CityVisitOrderRealignReqDto dto = new CityVisitOrderRealignReqDto(journey.getId(),
                List.of(new CityVisitOrder(a.getId(), 10),
                        new CityVisitOrder(b.getId(), 0),
                        new CityVisitOrder(c.getId(), 5)));

        mockMvc.perform(patch("/api/city-visit/order")
                        .with(accessToken(OWNER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        assertThat(reloadVisitOrder(b.getId())).isEqualTo(0);
        assertThat(reloadVisitOrder(c.getId())).isEqualTo(1);
        assertThat(reloadVisitOrder(a.getId())).isEqualTo(2);
    }

    @Test
    @DisplayName("PATCH /api/city-visit/order - order 값이 중복되면 400")
    void realignOrder_invalid() throws Exception {
        Journey journey = saveJourney(OWNER);
        CityVisit a = saveCityVisit(journey, LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 4));
        CityVisit b = saveCityVisit(journey, LocalDate.of(2026, 1, 5), LocalDate.of(2026, 1, 6));

        CityVisitOrderRealignReqDto dto = new CityVisitOrderRealignReqDto(journey.getId(),
                List.of(new CityVisitOrder(a.getId(), 0),
                        new CityVisitOrder(b.getId(), 0))); // 중복 order

        mockMvc.perform(patch("/api/city-visit/order")
                        .with(accessToken(OWNER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("INVALID_CITY_VISIT_ORDER"));
    }

    @Test
    @DisplayName("DELETE /api/city-visit/{id} - 소유자가 삭제하면 204")
    void delete_success() throws Exception {
        Journey journey = saveJourney(OWNER);
        CityVisit cityVisit = saveCityVisit(journey, LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 7));

        mockMvc.perform(delete("/api/city-visit/{cityVisitId}", cityVisit.getId())
                        .param("journeyId", journey.getId().toString())
                        .with(accessToken(OWNER)))
                .andExpect(status().isNoContent());

        assertThat(cityVisitRepository.findCityVisitByIdWithJourney(cityVisit.getId())).isEmpty();
    }

    @Test
    @DisplayName("DELETE /api/city-visit/{id} - 소유자가 아니면 403이고 삭제되지 않는다")
    void delete_accessDenied() throws Exception {
        Journey journey = saveJourney(OWNER);
        CityVisit cityVisit = saveCityVisit(journey, LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 7));

        mockMvc.perform(delete("/api/city-visit/{cityVisitId}", cityVisit.getId())
                        .param("journeyId", journey.getId().toString())
                        .with(accessToken(OTHER)))
                .andExpect(status().isForbidden());

        assertThat(cityVisitRepository.findCityVisitByIdWithJourney(cityVisit.getId())).isPresent();
    }
}
