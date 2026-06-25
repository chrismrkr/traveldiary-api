package kko.traveldiary_api.post.adaptor.infrastructure;

import kko.traveldiary_api.post.domain.PlacePoint;
import kko.traveldiary_api.post.domain.Post;
import kko.traveldiary_api.shared.Coordinate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(PostDatabaseRepository.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
record PostDatabaseRepositoryTest(PostDatabaseRepository postDatabaseRepository, PostJpaRepository jpaRepository) {

    @BeforeEach
    void init() {
        jpaRepository.deleteAll();
    }

    private Post samplePost(Long cityVisitId, String content) {
        PlacePoint placePoint = PlacePoint.create(
                "도쿄 스카이트리", "google", "place-skytree", new Coordinate(35.7100, 139.8107));
        return Post.create(cityVisitId, placePoint, content);
    }

    @Test
    @DisplayName("Post 엔티티를 저장할 수 있다")
    void save() {
        Post saved = postDatabaseRepository.save(samplePost(10L, "도쿄 첫째날"));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCityVisitId()).isEqualTo(10L);
        assertThat(saved.getContent()).isEqualTo("도쿄 첫째날");
        assertThat(saved.getPlacePoint().getPlaceName()).isEqualTo("도쿄 스카이트리");
        assertThat(saved.getPlacePoint().getCoordinate().getLatitude()).isEqualByComparingTo("35.7100");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getModifiedAt()).isNotNull();

        // 실제로 영속되었는지 확인
        assertThat(jpaRepository.findById(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("Post 엔티티를 Id로 조회할 수 있다")
    void findById() {
        Post saved = postDatabaseRepository.save(samplePost(10L, "도쿄 둘째날"));

        Optional<Post> found = postDatabaseRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
        assertThat(found.get().getCityVisitId()).isEqualTo(10L);
        assertThat(found.get().getContent()).isEqualTo("도쿄 둘째날");
        assertThat(found.get().getPlacePoint().getPlaceId()).isEqualTo("place-skytree");

        // 존재하지 않는 Id 는 비어있다
        assertThat(postDatabaseRepository.findById(99_999L)).isEmpty();
    }

    @Test
    @DisplayName("Post 엔티티 목록을 CityVisitId로 조회할 수 있다")
    void findByCityVisitId() {
        postDatabaseRepository.save(samplePost(10L, "글1"));
        postDatabaseRepository.save(samplePost(10L, "글2"));
        postDatabaseRepository.save(samplePost(20L, "다른 방문의 글")); // 다른 cityVisit → 제외되어야 한다

        List<Post> result = postDatabaseRepository.findByCityVisitId(10L);

        assertThat(result).hasSize(2)
                .extracting(Post::getContent)
                .containsExactlyInAnyOrder("글1", "글2");
    }

    @Test
    @DisplayName("Post 엔티티를 Id로 삭제할 수 있다")
    void deleteById() {
        Post saved = postDatabaseRepository.save(samplePost(10L, "삭제될 글"));

        postDatabaseRepository.delete(saved.getId());

        assertThat(postDatabaseRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    @DisplayName("Post 엔티티를 도메인 객체를 통해서 삭제할 수 있다")
    void delete() {
        Post saved = postDatabaseRepository.save(samplePost(10L, "도메인으로 삭제될 글"));

        postDatabaseRepository.delete(saved);

        assertThat(postDatabaseRepository.findById(saved.getId())).isEmpty();
    }
}
