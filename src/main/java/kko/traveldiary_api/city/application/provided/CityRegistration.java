package kko.traveldiary_api.city.application.provided;

import kko.traveldiary_api.city.domain.City;
import kko.traveldiary_api.shared.Coordinate;

public interface CityRegistration {
    City register(String name, String placeId, Coordinate coordinate);
    City register(City city);
}
