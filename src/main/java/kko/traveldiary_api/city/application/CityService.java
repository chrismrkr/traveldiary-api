package kko.traveldiary_api.city.application;

import kko.traveldiary_api.city.application.provided.CityFinder;
import kko.traveldiary_api.city.application.provided.CityRegistration;
import kko.traveldiary_api.city.application.required.CityRepository;
import kko.traveldiary_api.city.domain.City;
import kko.traveldiary_api.city.domain.CityNotReadyException;
import kko.traveldiary_api.shared.Coordinate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CityService implements CityFinder {
    private final CityRegistration cityRegisterService;
    private final CityRepository repository;

    @Override
    public City find(String name, String placeId, Coordinate coordinate) {
        return repository.findByCoordinate(coordinate)
                .orElseThrow(() -> {
                    cityRegisterService.register(name, placeId, coordinate);
                    throw new CityNotReadyException(coordinate);
                });
    }

}
