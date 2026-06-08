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
public class CityService implements CityFinder, CityRegistration {
    private final CityRepository repository;

    @Override
    public City find(String name, Coordinate coordinate) {
        return repository.findByCoordinate(coordinate)
                .orElseThrow(() -> {
                    // TODO. City Registration Init(Async)
                    doRegister(name, coordinate);
                    throw new CityNotReadyException(coordinate);
                });
    }

    @Override
    public void register(String name, Coordinate coordinate) {

    }

    private void doRegister(String name, Coordinate coordinate) {
        City newCity = City.builder()
                .name(name).coordinate(coordinate).build();
        repository.save(newCity);
    }
}
