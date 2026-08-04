package kko.traveldiary_api.city.domain;

/**
 * placeId 로 조회했으나 아직 등록된 City 가 없는 경우.
 */
public class CityNotFoundException extends RuntimeException {
    public CityNotFoundException(String placeId) {
        super("City not found for placeId: " + placeId);
    }
}
