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

}