package kko.traveldiary_api.post.application;

import kko.traveldiary_api.post.adaptor.infrastructure.PostJpaRepository;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class PostServiceTest {

    private static final Long OWNER_ID = 1L;
    private static final Long CITY_VISIT_A = 10L;
    private static final Long CITY_VISIT_B = 20L;

    @Autowired
    PostService postService;

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
        // 어떤 CityVisit이든 OWNER_ID가 소유한 것으로 간주 (journey 모듈과 분리)
        given(cityVisitAccessPort.findOwnerIdOfCityVisit(anyLong()))
                .willReturn(Optional.of(OWNER_ID));
    }

    private PlacePoint samplePlacePoint() {
        return PlacePoint.create(
                "도쿄 스카이트리", "google", "place-skytree", new Coordinate(35.7100, 139.8107));
    }

    @Test
    @DisplayName("Post를 Id로 조회할 수 있다")
    void search() {
        Post saved = postService.attach(OWNER_ID, CITY_VISIT_A, samplePlacePoint(), "도쿄 첫째날");

        Post found = postService.search(OWNER_ID, saved.getId());

        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getContent()).isEqualTo("도쿄 첫째날");
    }

    @Test
    @DisplayName("존재하지 않는 Id로 조회하면 예외가 발생한다")
    void search_notFound() {
        assertThatThrownBy(() -> postService.search(OWNER_ID, 99_999L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("CityVisitId로 해당 방문의 Post 목록만 조회한다")
    void searchByCityVisitId() {
        postService.attach(OWNER_ID, CITY_VISIT_A, samplePlacePoint(), "글1");
        postService.attach(OWNER_ID, CITY_VISIT_A, samplePlacePoint(), "글2");
        postService.attach(OWNER_ID, CITY_VISIT_B, samplePlacePoint(), "다른 방문의 글"); // 제외되어야 한다

        List<Post> result = postService.searchByCityVisitId(OWNER_ID, CITY_VISIT_A);

        assertThat(result).hasSize(2)
                .extracting(Post::getContent)
                .containsExactlyInAnyOrder("글1", "글2");
    }

    @Test
    @DisplayName("CityVisit에 Post를 추가(attach)할 수 있다")
    void attach() {
        Post result = postService.attach(OWNER_ID, CITY_VISIT_A, samplePlacePoint(), "도쿄 첫째날");

        assertThat(result.getId()).isNotNull();
        assertThat(result.getCityVisitId()).isEqualTo(CITY_VISIT_A);
        assertThat(result.getContent()).isEqualTo("도쿄 첫째날");
        assertThat(result.getPlacePoint().getPlaceName()).isEqualTo("도쿄 스카이트리");
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getModifiedAt()).isNotNull();

        // 실제로 영속되었는지 재조회로 확인
        Post reloaded = postRepository.findById(result.getId()).orElseThrow();
        assertThat(reloaded.getContent()).isEqualTo("도쿄 첫째날");
    }

    @Test
    @DisplayName("Post 내용을 수정할 수 있다")
    void updateContent() {
        Post saved = postService.attach(OWNER_ID, CITY_VISIT_A, samplePlacePoint(), "수정 전 내용");

        Post result = postService.updateContent(OWNER_ID, saved.getId(), "수정 후 내용");

        assertThat(result.getContent()).isEqualTo("수정 후 내용");

        Post reloaded = postRepository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.getContent()).isEqualTo("수정 후 내용");
    }

    @Test
    @DisplayName("존재하지 않는 Post의 내용을 수정하면 예외가 발생한다")
    void updateContent_notFound() {
        assertThatThrownBy(() -> postService.updateContent(OWNER_ID, 99_999L, "내용"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Post를 Id로 삭제(detach)할 수 있다")
    void detach() {
        Post saved = postService.attach(OWNER_ID, CITY_VISIT_A, samplePlacePoint(), "삭제될 글");

        postService.detach(OWNER_ID, saved.getId());

        assertThat(postRepository.findById(saved.getId())).isEmpty();
    }
}
