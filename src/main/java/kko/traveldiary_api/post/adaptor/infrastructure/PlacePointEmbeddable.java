package kko.traveldiary_api.post.adaptor.infrastructure;

import jakarta.persistence.*;
import kko.traveldiary_api.post.domain.PlacePoint;
import kko.traveldiary_api.shared.Coordinate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlacePointEmbeddable {
    @Column
    private String placeName;

    @Column
    private String provider;      // iOS 18 미만 등에서 null 가능

    @Column
    private String placeId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude",
                    column = @Column(name = "place_latitude")),
            @AttributeOverride(name = "longitude",
                    column = @Column(name = "place_longitude"))
    })
    private Coordinate coordinate;

    public PlacePointEmbeddable(String placeName, String provider,
                                String placeId, Coordinate coordinate) {
        this.placeName = placeName;
        this.provider = provider;
        this.placeId = placeId;
        this.coordinate = coordinate;
    }

    public static PlacePointEmbeddable from(PlacePoint p) {
        return new PlacePointEmbeddable(
                p.getPlaceName(), p.getProvider(), p.getPlaceId(), p.getCoordinate());
    }

    public PlacePoint toDomain() {
        return PlacePoint.create(placeName, provider, placeId, coordinate);
    }
}
