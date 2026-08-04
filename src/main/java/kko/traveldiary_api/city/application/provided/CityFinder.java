package kko.traveldiary_api.city.application.provided;

import kko.traveldiary_api.city.domain.City;
import kko.traveldiary_api.shared.Coordinate;

import java.util.Optional;

public interface CityFinder {
    City findByPlaceId(String placeId);
    City findOrRegister(String name, String placeId, Coordinate coordinate);
    City findByCoordinate(Coordinate coordinate);
    byte[] findImage(String imageId);
}
