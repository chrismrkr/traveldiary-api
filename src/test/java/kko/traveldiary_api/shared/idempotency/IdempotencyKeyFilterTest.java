package kko.traveldiary_api.shared.idempotency;

import com.fasterxml.jackson.databind.ObjectMapper;
import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.request.JourneyRegisterReqDto;
import kko.traveldiary_api.journey.domain.Visibility;
import kko.traveldiary_api.journey.adaptor.infrastructure.CityVisitJpaRepository;
import kko.traveldiary_api.journey.adaptor.infrastructure.JourneyJpaRepository;
import kko.traveldiary_api.journey.application.required.JourneyRepository;
import kko.traveldiary_api.shared.idempotency.infrastructure.IdempotencyKeyJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDate;
import java.util.UUID;

import static kko.traveldiary_api.shared.idempotency.IdempotencyKeyFilter.HEADER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class IdempotencyKeyFilterTest {

    private static final LocalDate START = LocalDate.of(2026, 1, 1);
    private static final LocalDate END = LocalDate.of(2026, 1, 10);
    private static final Long OWNER = 1L;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JourneyRepository journeyRepository;

    @Autowired
    JourneyJpaRepository journeyJpaRepository;

    @Autowired
    CityVisitJpaRepository cityVisitJpaRepository;

    @Autowired
    IdempotencyKeyJpaRepository idempotencyKeyJpaRepository;

    @BeforeEach
    @AfterEach
    void clean() {
        cityVisitJpaRepository.deleteAll();
        journeyJpaRepository.deleteAll();
        idempotencyKeyJpaRepository.deleteAll();
    }

    private static RequestPostProcessor accessToken(Long memberId) {
        return jwt().jwt(builder -> builder.subject(String.valueOf(memberId)));
    }

    private String registerBody() throws Exception {
        return objectMapper.writeValueAsString(
                new JourneyRegisterReqDto(START, END, "도쿄 여행", Visibility.PUBLIC));
    }

    @Test
    @DisplayName("POST에 Idempotency-Key 헤더가 없으면 400이고 리소스가 생성되지 않는다")
    void missingKey() throws Exception {
        mockMvc.perform(post("/api/journey")
                        .with(accessToken(OWNER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("IDEMPOTENCY_KEY_REQUIRED"));

        assertThat(journeyRepository.findByMemberId(OWNER)).isEmpty();
    }

    @Test
    @DisplayName("같은 Idempotency-Key로 두 번 호출하면 두 번째는 409이고 리소스는 하나만 생성된다")
    void duplicateKeyIsRejected() throws Exception {
        String key = UUID.randomUUID().toString();

        mockMvc.perform(post("/api/journey")
                        .with(accessToken(OWNER))
                        .header(HEADER, key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        // 응답을 저장하지 않으므로 재생 대신 409 로 거절한다.
        mockMvc.perform(post("/api/journey")
                        .with(accessToken(OWNER))
                        .header(HEADER, key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("DUPLICATE_IDEMPOTENCY_KEY"));

        // 핵심: 실제 리소스는 하나만 생성된다.
        assertThat(journeyRepository.findByMemberId(OWNER)).hasSize(1);
    }

    @Test
    @DisplayName("핸들러가 실패해도 멱등키는 남으므로 같은 키로는 다시 처리되지 않는다")
    void keyRemainsAfterHandlerFailure() throws Exception {
        String key = UUID.randomUUID().toString();

        // name 이 공백이라 검증 단계에서 400 으로 실패한다.
        String invalidBody = objectMapper.writeValueAsString(
                new JourneyRegisterReqDto(START, END, "  ", Visibility.PUBLIC));
        mockMvc.perform(post("/api/journey")
                        .with(accessToken(OWNER))
                        .header(HEADER, key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest());

        // 실패해도 키를 지우지 않기로 했으므로, 같은 키의 재시도는 409 다.
        assertThat(idempotencyKeyJpaRepository.findByIdempotencyKey(key)).isPresent();
        mockMvc.perform(post("/api/journey")
                        .with(accessToken(OWNER))
                        .header(HEADER, key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("DUPLICATE_IDEMPOTENCY_KEY"));

        assertThat(journeyRepository.findByMemberId(OWNER)).isEmpty();
    }

    @Test
    @DisplayName("서로 다른 Idempotency-Key는 각각 새 리소스를 생성한다")
    void differentKeysCreateSeparately() throws Exception {
        mockMvc.perform(post("/api/journey")
                        .with(accessToken(OWNER))
                        .header(HEADER, UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/journey")
                        .with(accessToken(OWNER))
                        .header(HEADER, UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody()))
                .andExpect(status().isOk());

        assertThat(journeyRepository.findByMemberId(OWNER)).hasSize(2);
    }
}
