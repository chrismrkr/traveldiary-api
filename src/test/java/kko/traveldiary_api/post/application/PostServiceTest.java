package kko.traveldiary_api.post.application;

import kko.traveldiary_api.post.adaptor.infrastructure.PostJpaRepository;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class PostServiceTest {

    @Autowired
    PostService postService;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostJpaRepository postJpaRepository;

    @BeforeEach
    @AfterEach
    void clean() {
        postJpaRepository.deleteAll();
    }

    private PlacePoint samplePlacePoint() {
        return PlacePoint.create(
                "도쿄 스카이트리", "google", "place-skytree", new Coordinate(35.7100, 139.8107));
    }

    @Test
    @DisplayName("Post를 Id로 조회할 수 있다")
    void search() {
        Post saved = postService.attach(10L, samplePlacePoint(), "도쿄 첫째날");

        Optional<Post> found = postService.search(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
        assertThat(found.get().getContent()).isEqualTo("도쿄 첫째날");
    }

    @Test
    @DisplayName("존재하지 않는 Id로 조회하면 비어있다")
    void search_notFound() {
        assertThat(postService.search(99_999L)).isEmpty();
    }

    @Test
    @DisplayName("CityVisitId로 해당 방문의 Post 목록만 조회한다")
    void searchByCityVisitId() {
        postService.attach(10L, samplePlacePoint(), "글1");
        postService.attach(10L, samplePlacePoint(), "글2");
        postService.attach(20L, samplePlacePoint(), "다른 방문의 글"); // 제외되어야 한다

        List<Post> result = postService.searchByCityVisitId(10L);

        assertThat(result).hasSize(2)
                .extracting(Post::getContent)
                .containsExactlyInAnyOrder("글1", "글2");
    }

    @Test
    @DisplayName("CityVisit에 Post를 추가(attach)할 수 있다")
    void attach() {
        Post result = postService.attach(10L, samplePlacePoint(), "도쿄 첫째날");

        assertThat(result.getId()).isNotNull();
        assertThat(result.getCityVisitId()).isEqualTo(10L);
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
        Post saved = postService.attach(10L, samplePlacePoint(), "수정 전 내용");

        Post result = postService.updateContent(saved.getId(), "수정 후 내용");

        assertThat(result.getContent()).isEqualTo("수정 후 내용");

        Post reloaded = postRepository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.getContent()).isEqualTo("수정 후 내용");
    }

    @Test
    @DisplayName("존재하지 않는 Post의 내용을 수정하면 예외가 발생한다")
    void updateContent_notFound() {
        assertThatThrownBy(() -> postService.updateContent(99_999L, "내용"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Post를 Id로 삭제(detach)할 수 있다")
    void detach() {
        Post saved = postService.attach(10L, samplePlacePoint(), "삭제될 글");

        postService.detach(saved.getId());

        assertThat(postService.search(saved.getId())).isEmpty();
    }
}
