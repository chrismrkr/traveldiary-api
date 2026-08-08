package kko.traveldiary_api.city.adaptor.inbound.internal;

import kko.traveldiary_api.city.application.provided.CityFinder;
import kko.traveldiary_api.city.domain.City;
import kko.traveldiary_api.journey.application.required.CityQueryPort;
import kko.traveldiary_api.shared.Coordinate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CityQueryAdaptor implements CityQueryPort {
    private final CityFinder cityFinder;
    @Override
    public CityInfo search(String cityName, String placeId, Coordinate coordinate) {
        City city = cityFinder.findOrRegister(cityName, placeId, coordinate);
        return new CityInfo(city.getId());
    }
}
