package kko.traveldiary_api.journey.application.required;

import kko.traveldiary_api.shared.Coordinate;

public interface CityQueryPort {
    CityInfo search(String cityName, String cityId, Coordinate coordinate);
    record CityInfo(Long cityId) {}
}
