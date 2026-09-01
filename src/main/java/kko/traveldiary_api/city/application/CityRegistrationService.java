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
            CityImage image = imageGenerator.generate(city, cityDescription);

            imageStorage.save(image.id(), image.imageBytes());

            city.saveDetails(cityDescription, image.id());
            city.setStatus(City.Status.READY);
            repository.save(city);

        } catch (NoSuchElementException e) {
            log.warn("City not found, skipping detail generation. placeId={}", request.placeId());
        }
        catch (Exception e) {
            // 비동기 스레드에서 도는 데다 호출자에게 전파되지 않으므로, 여기서 남기지 않으면 원인이 사라진다.
            log.error("Failed to generate city detail. placeId={}, cityId={}",
                    request.placeId(), city == null ? null : city.getId(), e);
            if (city != null) {
                city.setStatus(City.Status.FAILED);
                repository.save(city);
            }
            // TODO Requiring Retry Policy
        }
    }

    @Override
    public City register(City city) {
        return repository.save(city);
    }
}
