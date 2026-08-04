package kko.traveldiary_api.city.adaptor.inbound.controller.dto.response;

import kko.traveldiary_api.city.domain.City;
import kko.traveldiary_api.city.domain.CityDescription;
import kko.traveldiary_api.shared.Coordinate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CityResponseDto {
    private Long cityId;
    private String placeId;
    private String name;
    private String overview;
    private String historyAndCulture;
    private String funFact;
    private String localTip;
    private String cityImageId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private City.Status status;

    @Builder
    private CityResponseDto(Long cityId, String placeId, String name, String overview, String historyAndCulture,
                            String funFact, String localTip, String cityImageId,
                            BigDecimal latitude, BigDecimal longitude, City.Status status) {
        this.cityId = cityId;
        this.placeId = placeId;
        this.name = name;
        this.overview = overview;
        this.historyAndCulture = historyAndCulture;
        this.funFact = funFact;
        this.localTip = localTip;
        this.cityImageId = cityImageId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
    }

    public static CityResponseDto convert(City city) {
        CityDescription description = city.getCityDescription();
        Coordinate coordinate = city.getCoordinate();
        return CityResponseDto.builder()
                .cityId(city.getId())
                .placeId(city.getPlaceId())
                .name(city.getName())
                .overview(description == null ? null : description.overview())
                .historyAndCulture(description == null ? null : description.historyAndCulture())
                .funFact(description == null ? null : description.funFact())
                .localTip(description == null ? null : description.localTip())
                .cityImageId(city.getCityImageId())
                .latitude(coordinate == null ? null : coordinate.getLatitude())
                .longitude(coordinate == null ? null : coordinate.getLongitude())
                .status(city.getStatus())
                .build();
    }
}
