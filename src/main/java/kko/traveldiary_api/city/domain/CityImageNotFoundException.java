package kko.traveldiary_api.city.domain;

/**
 * imageId 에 해당하는 이미지가 저장소에 없는 경우.
 */
public class CityImageNotFoundException extends RuntimeException {
    public CityImageNotFoundException(String imageId) {
        super("City image not found: " + imageId);
    }
}
