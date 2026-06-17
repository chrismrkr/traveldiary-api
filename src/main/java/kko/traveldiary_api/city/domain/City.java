package kko.traveldiary_api.city.domain;

import kko.traveldiary_api.shared.Coordinate;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class City {
    private Long id;
    private String placeId;
    private String name;
    private String description;
    private String cityImageId;
    private Coordinate coordinate;
    @Setter
    private Status status;

    @Builder
    private City(Long id, String name, String placeId, String description, String cityImageId, Coordinate coordinate, Status status) {
        this.id = id;
        this.name = name;
        this.placeId = placeId;
        this.description = description;
        this.cityImageId = cityImageId;
        this.coordinate = coordinate;
        this.status = status;
    }

    public void saveDetails(String description, String imageId) {
        this.description = description;
        this.cityImageId = imageId;
    }


    public enum Status { PENDING, READY, FAILED }
}
