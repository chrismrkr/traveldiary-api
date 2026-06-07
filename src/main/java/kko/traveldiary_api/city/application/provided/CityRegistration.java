package kko.traveldiary_api.city.application.provided;

import kko.traveldiary_api.shared.Coordinate;

public interface CityRegistration {
    void register(String name, Coordinate coordinate);
}
