package kko.traveldiary_api.city.domain;

import kko.traveldiary_api.shared.Coordinate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class City {
    private Long id;
    private String name;
    private String description;
    private String cityImageId;
    private Coordinate coordinate;
    @Builder
    private City(String name, String description, String cityImageId, Coordinate coordinate) {
        this.name = name;
        this.description = description;
        this.cityImageId = cityImageId;
        this.coordinate = coordinate;
    }
}
