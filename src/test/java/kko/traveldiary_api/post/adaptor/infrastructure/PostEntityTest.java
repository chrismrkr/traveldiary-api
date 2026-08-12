package kko.traveldiary_api.post.adaptor.infrastructure;

import kko.traveldiary_api.post.domain.PlacePoint;
import kko.traveldiary_api.post.domain.Post;
import kko.traveldiary_api.shared.Coordinate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class PostEntityTest {

    @Test
    @DisplayName("Post Entity를 Domain 객체로 변환할 수 있다")
    void convertEntityToDomain() {
        // given
        PlacePointEmbeddable placePointEmbeddable = new PlacePointEmbeddable("place-1", "provider-1", "placeId-1", new Coordinate(12.01, 15.01));
        PostEntity entity = new PostEntity(1L, 2L, placePointEmbeddable, "content1", LocalDateTime.now(), LocalDateTime.now());

        // when
        Post domain = entity.toDomain();

        // then
        Assertions.assertEquals(entity.getId(), domain.getId());
        Assertions.assertEquals(entity.getCityVisitId(), domain.getCityVisitId());
        Assertions.assertEquals(entity.getPlacePoint().getPlaceName(), domain.getPlacePoint().getPlaceName());
        Assertions.assertEquals(entity.getPlacePoint().getPlaceId(), domain.getPlacePoint().getPlaceId());
        Assertions.assertEquals(entity.getPlacePoint().getProvider(), domain.getPlacePoint().getProvider());
        Assertions.assertEquals(entity.getPlacePoint().getCoordinate().getLatitude(), domain.getPlacePoint().getCoordinate().getLatitude());
        Assertions.assertEquals(entity.getPlacePoint().getCoordinate().getLongitude(), domain.getPlacePoint().getCoordinate().getLongitude());
        Assertions.assertEquals(entity.getContent(), domain.getContent());
    }

    @Test
    @DisplayName("Post Domain 객체를 Entity로 변환할 수 있다")
    void convertDomainToEntity() {
        // given
        PlacePoint placePoint = PlacePoint.create("place-1", "provider-1", "placeId-1", new Coordinate(12.01, 15.01));
        Post domain = Post.create(1L, 2L, placePoint, "content-1", LocalDateTime.now(), LocalDateTime.now());

        // when
        PostEntity entity = PostEntity.from(domain);

        // then
        Assertions.assertEquals(entity.getId(), domain.getId());
        Assertions.assertEquals(entity.getCityVisitId(), domain.getCityVisitId());
        Assertions.assertEquals(entity.getPlacePoint().getPlaceName(), domain.getPlacePoint().getPlaceName());
        Assertions.assertEquals(entity.getPlacePoint().getPlaceId(), domain.getPlacePoint().getPlaceId());
        Assertions.assertEquals(entity.getPlacePoint().getProvider(), domain.getPlacePoint().getProvider());
        Assertions.assertEquals(entity.getPlacePoint().getCoordinate().getLatitude(), domain.getPlacePoint().getCoordinate().getLatitude());
        Assertions.assertEquals(entity.getPlacePoint().getCoordinate().getLongitude(), domain.getPlacePoint().getCoordinate().getLongitude());
        Assertions.assertEquals(entity.getContent(), domain.getContent());
    }

    @Test
    @DisplayName("placePoint가 null인 Entity는 비어있는 PlacePoint를 가진 Domain으로 변환된다")
    void convertEntityToDomain_withNullPlacePoint() {
        // given: 장소 컬럼이 전부 null 이면 Hibernate 는 embeddable 을 null 로 넘긴다.
        PostEntity entity = new PostEntity(1L, 2L, null, "content1", LocalDateTime.now(), LocalDateTime.now());

        // when
        Post domain = entity.toDomain();

        // then: null 이 아니라 모든 필드가 비어있는 PlacePoint 여야 한다.
        Assertions.assertNotNull(domain.getPlacePoint());
        Assertions.assertNull(domain.getPlacePoint().getPlaceName());
        Assertions.assertNull(domain.getPlacePoint().getProvider());
        Assertions.assertNull(domain.getPlacePoint().getPlaceId());
        Assertions.assertNull(domain.getPlacePoint().getCoordinate());
        Assertions.assertEquals(entity.getContent(), domain.getContent());
    }

    @Test
    @DisplayName("placeName이 없는 Entity도 Domain 객체로 변환할 수 있다")
    void convertEntityToDomain_withoutPlaceName() {
        // given
        PlacePointEmbeddable placePointEmbeddable = new PlacePointEmbeddable(null, "provider-1", "placeId-1", null);
        PostEntity entity = new PostEntity(1L, 2L, placePointEmbeddable, "content1", LocalDateTime.now(), LocalDateTime.now());

        // when
        Post domain = entity.toDomain();

        // then
        Assertions.assertNull(domain.getPlacePoint().getPlaceName());
        Assertions.assertEquals("provider-1", domain.getPlacePoint().getProvider());
        Assertions.assertEquals("placeId-1", domain.getPlacePoint().getPlaceId());
        Assertions.assertNull(domain.getPlacePoint().getCoordinate());
    }

    @Test
    @DisplayName("placeName이 없는 Domain 객체도 Entity로 변환할 수 있다")
    void convertDomainToEntity_withoutPlaceName() {
        // given
        PlacePoint placePoint = PlacePoint.create(null, "provider-1", "placeId-1", null);
        Post domain = Post.create(1L, 2L, placePoint, "content-1", LocalDateTime.now(), LocalDateTime.now());

        // when
        PostEntity entity = PostEntity.from(domain);

        // then
        Assertions.assertNull(entity.getPlacePoint().getPlaceName());
        Assertions.assertEquals("provider-1", entity.getPlacePoint().getProvider());
        Assertions.assertEquals("placeId-1", entity.getPlacePoint().getPlaceId());
        Assertions.assertNull(entity.getPlacePoint().getCoordinate());
    }

    @Test
    @DisplayName("비어있는 PlacePoint는 Entity -> Domain 왕복 후에도 유지된다")
    void convertRoundTrip_withEmptyPlacePoint() {
        // given
        Post domain = Post.create(1L, 2L, PlacePoint.empty(), "content-1", LocalDateTime.now(), LocalDateTime.now());

        // when: Entity 로 변환했다가 (모든 컬럼이 null 인 상황을 흉내내어) 다시 Domain 으로 되돌린다.
        PostEntity entity = PostEntity.from(domain);
        Post restored = new PostEntity(entity.getId(), entity.getCityVisitId(), null,
                entity.getContent(), entity.getCreatedAt(), entity.getModifiedAt()).toDomain();

        // then
        Assertions.assertNotNull(restored.getPlacePoint());
        Assertions.assertNull(restored.getPlacePoint().getPlaceName());
        Assertions.assertEquals(domain.getContent(), restored.getContent());
    }

}