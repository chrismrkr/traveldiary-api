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
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CityRegistrationService implements CityRegistration, CityDetailRegistration {
    private final CityRepository repository;
    private final CityRegistrationEventPublisher eventPublisher;
    private final CityDescriptionGenerator cityDescriptionGenerator;
    private final CityImageGenerator imageGenerator;
    private final CityImageStoragePort imageStorage;

    @Override
    public City register(String name, String placeId, Coordinate coordinate) {
        City city = null;
        try {
            city = City.builder()
                    .name(name).placeId(placeId).coordinate(coordinate)
                    .status(City.Status.PENDING).build();
            city = repository.save(city);
            eventPublisher.publish(new CityGenerateRequest(name, placeId, coordinate));
        } catch (DataIntegrityViolationException ignored) {
            return repository.findByCoordinate(coordinate)
                    .orElseThrow(() -> new IllegalStateException(
                            "City conflicted on unique constraint but not found by coordinate: "
                                    + coordinate.getLatitude() + ", " + coordinate.getLongitude()));
        }
        return city;
    }

    @Override
    public void registerDetail(CityGenerateRequest request) {
        City city = null;
        try {
            city = repository.findByPlaceId(request.placeId()).orElseThrow();

            CityDescription cityDescription = cityDescriptionGenerator.generate(city);
            CityImage image = imageGenerator.generate(cityDescription);

            log.debug("[CityDescription funFact] {}", cityDescription.funFact());
            log.debug("[CityDescription historyAndCulture] {}", cityDescription.historyAndCulture());
            log.debug("[CityDescription localTip] {}", cityDescription.localTip());
            log.debug("[CityDescription overview] {}", cityDescription.overview());

            log.debug("[Image Bytes] {}", image.imageBytes());

            imageStorage.save(image.id(), image.imageBytes());

            city.saveDetails(cityDescription, image.id());
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
    public City register(City city) {
        return repository.save(city);
    }
}
