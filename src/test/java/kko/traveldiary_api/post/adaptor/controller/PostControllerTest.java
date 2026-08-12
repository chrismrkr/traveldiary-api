package kko.traveldiary_api.post.adaptor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kko.traveldiary_api.post.adaptor.infrastructure.PostJpaRepository;
import kko.traveldiary_api.post.adaptor.inbound.controller.dto.request.PostAttachReqDto;
import kko.traveldiary_api.post.adaptor.inbound.controller.dto.request.PostContentsModifyDto;
import kko.traveldiary_api.post.application.required.CityVisitAccessPort;
import kko.traveldiary_api.post.application.required.PostRepository;
import kko.traveldiary_api.post.domain.PlacePoint;
import kko.traveldiary_api.post.domain.Post;
import kko.traveldiary_api.shared.Coordinate;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

    private static final Long OWNER = 1L;
    private static final Long OTHER = 2L;
    private static final Long CITY_VISIT_A = 10L;
    private static final Long CITY_VISIT_B = 20L;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostJpaRepository postJpaRepository;

    @MockitoBean
    CityVisitAccessPort cityVisitAccessPort;

    @BeforeEach
    @AfterEach
    void clean() {
        postJpaRepository.deleteAll();
    }

    @BeforeEach
    void stubOwnership() {
        // 어떤 CityVisit이든 OWNER가 소유한 것으로 간주 (journey 모듈과 분리)
        given(cityVisitAccessPort.findOwnerIdOfCityVisit(anyLong()))
                .willReturn(Optional.of(OWNER));
    }

    /** access token 대신 subject(=memberId) 를 담은 JWT principal 을 주입한다. */
    private static RequestPostProcessor accessToken(Long memberId) {
        return jwt().jwt(builder -> builder.subject(String.valueOf(memberId)));
    }

    private Post savePost(Long cityVisitId, String content) {
        PlacePoint placePoint = PlacePoint.create(
                "도쿄 스카이트리", "google", "place-skytree", new Coordinate(35.7100, 139.8107));
        return postRepository.save(Post.create(cityVisitId, placePoint, content));
    }

    private PostAttachReqDto attachReqDto(Long cityVisitId, String content) {
        return new PostAttachReqDto(
                cityVisitId, "도쿄 스카이트리", "google", "place-skytree",
                BigDecimal.valueOf(35.7100), BigDecimal.valueOf(139.8107), content);
    }

    @Test
    @DisplayName("GET /api/post?cityVisitId - 해당 방문의 Post 목록만 200으로 반환한다")
    void findList() throws Exception {
        savePost(CITY_VISIT_A, "글1");
        savePost(CITY_VISIT_A, "글2");
        savePost(CITY_VISIT_B, "다른 방문의 글");

        mockMvc.perform(get("/api/post")
                        .param("cityVisitId", String.valueOf(CITY_VISIT_A))
                        .with(accessToken(OWNER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/post/{postId} - 소유자가 단건 조회하면 200과 Post를 반환한다")
    void findOne() throws Exception {
        Post saved = savePost(CITY_VISIT_A, "도쿄 첫째날");

        mockMvc.perform(get("/api/post/{postId}", saved.getId())
                        .with(accessToken(OWNER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.postId").value(saved.getId()))
                .andExpect(jsonPath("$.data.content").value("도쿄 첫째날"))
                .andExpect(jsonPath("$.data.placePoint.placeName").value("도쿄 스카이트리"));
    }

    @Test
    @DisplayName("GET /api/post/{postId} - 존재하지 않는 Post면 404")
    void findOne_notFound() throws Exception {
        mockMvc.perform(get("/api/post/{postId}", 99_999L)
                        .with(accessToken(OWNER)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/post/{postId} - 소유자가 아니면 403")
    void findOne_accessDenied() throws Exception {
        Post saved = savePost(CITY_VISIT_A, "도쿄 첫째날");

        mockMvc.perform(get("/api/post/{postId}", saved.getId())
                        .with(accessToken(OTHER)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value("NOT_OWNED_POST_ACCESS"));
    }

    @Test
    @DisplayName("POST /api/post - Post를 추가하면 200과 저장된 Post를 반환한다")
    void attach() throws Exception {
        PostAttachReqDto dto = attachReqDto(CITY_VISIT_A, "도쿄 첫째날");

        mockMvc.perform(post("/api/post")
                        .header("Idempotency-Key", java.util.UUID.randomUUID().toString())
                        .with(accessToken(OWNER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.postId").isNumber())
                .andExpect(jsonPath("$.data.cityVisitId").value(CITY_VISIT_A))
                .andExpect(jsonPath("$.data.content").value("도쿄 첫째날"));

        assertThat(postRepository.findByCityVisitId(CITY_VISIT_A)).hasSize(1);
    }

    @Test
    @DisplayName("POST /api/post - 좌표(latitude/longitude)가 없어도 추가된다")
    void attach_withoutCoordinate() throws Exception {
        PostAttachReqDto dto = new PostAttachReqDto(
                CITY_VISIT_A, "도쿄 스카이트리", "google", "place-skytree", null, null, "좌표 없는 글");

        mockMvc.perform(post("/api/post")
                        .header("Idempotency-Key", java.util.UUID.randomUUID().toString())
                        .with(accessToken(OWNER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.placePoint.placeName").value("도쿄 스카이트리"));

        // 좌표 없이도 영속되고, 재조회 시 coordinate 는 null 이다.
        Post reloaded = postRepository.findByCityVisitId(CITY_VISIT_A).get(0);
        assertThat(reloaded.getPlacePoint().getCoordinate()).isNull();
    }

    @Test
    @DisplayName("POST /api/post - CityVisit이 존재하지 않으면 404")
    void attach_cityVisitNotFound() throws Exception {
        given(cityVisitAccessPort.findOwnerIdOfCityVisit(CITY_VISIT_A))
                .willReturn(Optional.empty());
        PostAttachReqDto dto = attachReqDto(CITY_VISIT_A, "도쿄 첫째날");

        mockMvc.perform(post("/api/post")
                        .header("Idempotency-Key", java.util.UUID.randomUUID().toString())
                        .with(accessToken(OWNER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());

        assertThat(postRepository.findByCityVisitId(CITY_VISIT_A)).isEmpty();
    }

    @Test
    @DisplayName("POST /api/post - 소유자가 아니면 403이고 저장되지 않는다")
    void attach_accessDenied() throws Exception {
        PostAttachReqDto dto = attachReqDto(CITY_VISIT_A, "도쿄 첫째날");

        mockMvc.perform(post("/api/post")
                        .header("Idempotency-Key", java.util.UUID.randomUUID().toString())
                        .with(accessToken(OTHER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value("NOT_OWNED_POST_ACCESS"));

        assertThat(postRepository.findByCityVisitId(CITY_VISIT_A)).isEmpty();
    }

    @Test
    @DisplayName("PATCH /api/post - 내용을 수정하면 200과 수정된 Post를 반환한다")
    void updateContent() throws Exception {
        Post saved = savePost(CITY_VISIT_A, "수정 전 내용");
        PostContentsModifyDto dto = new PostContentsModifyDto(saved.getId(), "수정 후 내용");

        mockMvc.perform(patch("/api/post")
                        .with(accessToken(OWNER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.content").value("수정 후 내용"));

        assertThat(postRepository.findById(saved.getId()).orElseThrow().getContent())
                .isEqualTo("수정 후 내용");
    }

    @Test
    @DisplayName("PATCH /api/post - 존재하지 않는 Post면 404")
    void updateContent_notFound() throws Exception {
        PostContentsModifyDto dto = new PostContentsModifyDto(99_999L, "내용");

        mockMvc.perform(patch("/api/post")
                        .with(accessToken(OWNER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/post/{postId} - 소유자가 삭제하면 204")
    void detach() throws Exception {
        Post saved = savePost(CITY_VISIT_A, "삭제될 글");

        mockMvc.perform(delete("/api/post/{postId}", saved.getId())
                        .with(accessToken(OWNER)))
                .andExpect(status().isNoContent());

        assertThat(postRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    @DisplayName("DELETE /api/post/{postId} - 소유자가 아니면 403이고 삭제되지 않는다")
    void detach_accessDenied() throws Exception {
        Post saved = savePost(CITY_VISIT_A, "삭제될 글");

        mockMvc.perform(delete("/api/post/{postId}", saved.getId())
                        .with(accessToken(OTHER)))
                .andExpect(status().isForbidden());

        assertThat(postRepository.findById(saved.getId())).isPresent();
    }
}
