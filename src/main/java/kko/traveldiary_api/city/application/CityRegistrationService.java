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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CityRegistrationService implements CityRegistration, CityDetailRegistration {
    private final CityRepository repository;
    private final CityRegistrationEventPublisher eventPublisher;
    private final CityDescriptionGenerator cityDescriptionGenerator;
    private final CityImageGenerator imageGenerator;
    private final CityImageStoragePort imageStorage;

    @Override
    @Transactional
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
        City city = repository.findByPlaceId(request.placeId())
                .orElseThrow(() -> new IllegalStateException(""));

        // TODO 트랜잭션 내에 생성형 AI 호출이 병목을 일으키진 않을지 검토 필요
        CityDescription cityDescription = cityDescriptionGenerator.generate(city);
        CityImage image = imageGenerator.generate(cityDescription.summary());

        imageStorage.save(image.id(), image.imageBytes());

        city.setDetails(cityDescription.summary(), image.id());
        city.setReady();
        repository.save(city);
    }

    @Override
    public void register(City city) {
        repository.save(city);
    }
}
