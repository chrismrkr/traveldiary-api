package kko.traveldiary_api.city.adaptor.webapi;

import kko.traveldiary_api.city.application.required.CityDetailGenerator;
import kko.traveldiary_api.city.domain.City;
import kko.traveldiary_api.shared.Coordinate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

public class CityDetailAIGenerator implements CityDetailGenerator {

    @Override
    public City generateDetail(City city) {
        return null;
    }
}
