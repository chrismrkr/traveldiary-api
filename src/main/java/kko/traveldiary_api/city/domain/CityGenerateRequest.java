package kko.traveldiary_api.city.domain;

import kko.traveldiary_api.shared.Coordinate;

public record CityGenerateRequest(String name, String placeId, Coordinate coordinate) { }
