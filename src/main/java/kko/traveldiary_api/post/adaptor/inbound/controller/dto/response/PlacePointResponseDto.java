package kko.traveldiary_api.post.adaptor.inbound.controller.dto.response;

import kko.traveldiary_api.post.domain.PlacePoint;
import kko.traveldiary_api.shared.Coordinate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlacePointResponseDto {
    private String placeName;
    private String provider;
    private String placeId;
    private BigDecimal latitude;
    private BigDecimal longitude;

    @Builder
    private PlacePointResponseDto(String placeName, String provider, String placeId, BigDecimal latitude, BigDecimal longitude) {
        this.placeName = placeName;
        this.provider = provider;
        this.placeId = placeId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static PlacePointResponseDto convert(PlacePoint placePoint) {
        Coordinate coordinate = placePoint.getCoordinate();
        return PlacePointResponseDto.builder()
                .placeName(placePoint.getPlaceName())
                .provider(placePoint.getProvider())
                .placeId(placePoint.getPlaceId())
                .latitude(coordinate.getLatitude())
                .longitude(coordinate.getLongitude())
                .build();
    }
}
