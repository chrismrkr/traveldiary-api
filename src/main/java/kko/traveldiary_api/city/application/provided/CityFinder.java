package kko.traveldiary_api.city.application.provided;

import kko.traveldiary_api.city.domain.City;
import kko.traveldiary_api.shared.Coordinate;

import java.util.Optional;

public interface CityFinder {
    City find(String name, Coordinate coordinate);
}
