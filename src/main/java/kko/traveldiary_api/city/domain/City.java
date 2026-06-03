package kko.traveldiary_api.city.domain;

import kko.traveldiary_api.shared.Coordinate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.boot.model.source.spi.IdentifierSource;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class City {
    private Long id;
    private String name;
    private String description;
    private String cityImageId;
    private Coordinate coordinate;
    private City(String name, String description, String cityImageId, Coordinate coordinate) {
        this.name = name;
        this.description = description;
        this.cityImageId = cityImageId;
        this.coordinate = coordinate;
    }
    public static City create(String name, String description, String cityImageId, Coordinate coordinate) {
        return new City(name, description, cityImageId, coordinate);
    }
}
