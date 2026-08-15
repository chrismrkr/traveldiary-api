package kko.traveldiary_api.post.adaptor.infrastructure;

import kko.traveldiary_api.post.domain.PlacePoint;
import kko.traveldiary_api.post.domain.Post;
import kko.traveldiary_api.shared.Coordinate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(PostDatabaseRepository.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
record PostDatabaseRepositoryTest(PostDatabaseRepository postDatabaseRepository, PostJpaRepository jpaRepository,
                                 TestEntityManager em) {

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
    @DisplayName("장소 정보가 모두 비어 있는 Post도 저장 후 다시 조회할 수 있다")
    void findById_withEmptyPlacePoint() {
        Post saved = postDatabaseRepository.save(
                Post.create(10L, PlacePoint.create(null, null, null, null), "장소 없는 글"));
        // 컬럼이 전부 null 이면 Hibernate 는 embeddable 자체를 null 로 매핑한다.
        // 영속성 컨텍스트를 비워야 캐시된 인스턴스가 아니라 DB 에서 다시 읽는다.
        em.flush();
        em.clear();

        Optional<Post> found = postDatabaseRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getContent()).isEqualTo("장소 없는 글");
        assertThat(found.get().getPlacePoint()).isNotNull();
        assertThat(found.get().getPlacePoint().getPlaceName()).isNull();
        assertThat(found.get().getPlacePoint().getProvider()).isNull();
        assertThat(found.get().getPlacePoint().getPlaceId()).isNull();
        assertThat(found.get().getPlacePoint().getCoordinate()).isNull();
    }

    @Test
    @DisplayName("placeName 없이 placeId만 있어도 저장 후 다시 조회할 수 있다")
    void findById_withoutPlaceName() {
        Post saved = postDatabaseRepository.save(
                Post.create(10L, PlacePoint.create(null, "google", "place-skytree", null), "이름 없는 장소의 글"));
        em.flush();
        em.clear();

        Optional<Post> found = postDatabaseRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getPlacePoint().getPlaceName()).isNull();
        assertThat(found.get().getPlacePoint().getPlaceId()).isEqualTo("place-skytree");
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

    @Test
    @DisplayName("여러 Post 엔티티들을 CityVisitId로 삭제할 수 있다")
    void deleteByCityVisitId() {
        Post saved1 = postDatabaseRepository.save(samplePost(10L, "CityVisitId로 삭제될 글"));
        Post saved2 = postDatabaseRepository.save(samplePost(20L, "CityVisitId로 삭제될 글"));
        Post saved3 = postDatabaseRepository.save(samplePost(20L, "CityVisitId로 삭제될 글"));
        Post saved4 = postDatabaseRepository.save(samplePost(20L, "CityVisitId로 삭제될 글"));
        Post saved5 = postDatabaseRepository.save(samplePost(20L, "CityVisitId로 삭제될 글"));

        postDatabaseRepository.deleteByCityVisitId(20L);

        assertThat(postDatabaseRepository.findByCityVisitId(20L)).isEmpty();
        assertThat(postDatabaseRepository.findByCityVisitId(10L)).isNotEmpty();
    }
}
