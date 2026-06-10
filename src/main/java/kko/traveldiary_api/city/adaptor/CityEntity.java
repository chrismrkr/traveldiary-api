package kko.traveldiary_api.city.adaptor;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import kko.traveldiary_api.city.domain.City;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 도메인 {@link City} 의 영속성 엔티티.
 * 좌표(latitude, longitude) 와 placeId 의 조합을 유니크 키로 갖는다.
 */
@Entity
@Table(
        name = "city",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_city_coordinate", columnNames = {"latitude", "longitude"}),
                @UniqueConstraint(name = "uk_city_place_id", columnNames = {"place_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "place_id", nullable = false)
    private String placeId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "city_image_id")
    private String cityImageId;

    @Embedded
    private CoordinateEmbeddable coordinate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private City.Status status;

    private CityEntity(Long id, String placeId, String name, String description,
                       String cityImageId, CoordinateEmbeddable coordinate, City.Status status) {
        this.id = id;
        this.placeId = placeId;
        this.name = name;
        this.description = description;
        this.cityImageId = cityImageId;
        this.coordinate = coordinate;
        this.status = status;
    }

    public static CityEntity from(City city) {
        return new CityEntity(
                city.getId(),
                city.getPlaceId(),
                city.getName(),
                city.getDescription(),
                city.getCityImageId(),
                CoordinateEmbeddable.fromDomain(city.getCoordinate()),
                city.getStatus()
        );
    }

    public City toDomain() {
        return City.builder()
                .id(id)
                .name(name)
                .placeId(placeId)
                .description(description)
                .cityImageId(cityImageId)
                .coordinate(coordinate.toDomain())
                .status(status)
                .build();
    }
}
