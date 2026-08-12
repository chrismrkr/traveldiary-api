package kko.traveldiary_api.post.domain;

import kko.traveldiary_api.shared.Coordinate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlacePoint {

    private String placeName;

    private String provider;
    private String placeId;

    private Coordinate coordinate;

    public static PlacePoint create(String placeName, String provider, String placeId, Coordinate coordinate) {
        return new PlacePoint(placeName, provider, placeId, coordinate);
    }

    /** 장소 정보가 하나도 없는 PlacePoint. 모든 필드가 선택값이므로 비어있는 상태도 유효하다. */
    public static PlacePoint empty() {
        return new PlacePoint(null, null, null, null);
    }

    private PlacePoint(String placeName, String provider, String placeId, Coordinate coordinate) {
        this.placeName = placeName;
        this.provider = provider;
        this.placeId = placeId;
        this.coordinate = coordinate;
    }
}
