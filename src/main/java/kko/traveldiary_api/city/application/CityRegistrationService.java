package kko.traveldiary_api.city.application;

import kko.traveldiary_api.city.application.provided.CityDetailRegistration;
import kko.traveldiary_api.city.application.provided.CityRegistration;
import kko.traveldiary_api.city.application.required.*;
import kko.traveldiary_api.city.domain.City;
import kko.traveldiary_api.city.domain.CityDescription;
import kko.traveldiary_api.city.domain.CityGenerateRequest;
import kko.traveldiary_api.city.domain.CityImage;
import kko.traveldiary_api.shared.Coordinate;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CityRegistrationService implements CityRegistration, CityDetailRegistration {
    private final CityRepository repository;
    private final CityRegistrationEventPublisher eventPublisher;
    private final CityDescriptionGenerator cityDescriptionGenerator;
    private final CityImageGenerator imageGenerator;
    private final CityImageStoragePort imageStorage;

    @Override
    public void register(String name, String placeId, Coordinate coordinate) {
        try {
            City newCity = City.builder()
                    .name(name).placeId(placeId).coordinate(coordinate)
                    .status(City.Status.PENDING).build();
            repository.save(newCity);
            eventPublisher.publish(new CityGenerateRequest(name, placeId, coordinate));
        } catch (DataIntegrityViolationException ignored) { }
    }

    @Override
    public void registerDetail(CityGenerateRequest request) {
        City city = null;
        try {
            city = repository.findByPlaceId(request.placeId()).orElseThrow();

            CityDescription cityDescription = cityDescriptionGenerator.generate(city);
            CityImage image = imageGenerator.generate(cityDescription);

            imageStorage.save(image.id(), image.imageBytes());


            // TODO cityDescription
//            city.saveDetails(cityDescription.summary(), image.id());
            city.setStatus(City.Status.READY);
            repository.save(city);
        } catch (NoSuchElementException ignored) { }
        catch (Exception e) {
            assert city != null;
            city.setStatus(City.Status.FAILED);
            repository.save(city);
            // TODO Requiring Retry Policy
        }
    }

    @Override
    public void register(City city) {
        repository.save(city);
    }
}
