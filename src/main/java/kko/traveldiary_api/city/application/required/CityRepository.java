package kko.traveldiary_api.city.application.required;

import kko.traveldiary_api.city.domain.City;
import kko.traveldiary_api.shared.Coordinate;

import java.util.Optional;

public interface CityRepository {
    Optional<City> findByCoordinate(Coordinate coordinate);
    void save(City city);
}
