package kko.traveldiary_api.city.application;

import kko.traveldiary_api.city.application.provided.CityDetailRegistration;
import kko.traveldiary_api.city.application.provided.CityRegistration;
import kko.traveldiary_api.city.application.required.CityDetailGenerator;
import kko.traveldiary_api.city.application.required.CityImageStoragePort;
import kko.traveldiary_api.city.application.required.CityRegistrationEventPublisher;
import kko.traveldiary_api.city.application.required.CityRepository;
import kko.traveldiary_api.city.domain.City;
import kko.traveldiary_api.city.domain.CityGenerateRequest;
import kko.traveldiary_api.city.domain.CityImageDetail;
import kko.traveldiary_api.shared.Coordinate;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CityRegistrationService implements CityRegistration, CityDetailRegistration {
    private final CityRepository repository;
    private final CityRegistrationEventPublisher eventPublisher;
    private final CityDetailGenerator cityDetailGenerator;
    private final CityImageStoragePort cityImageStorage;

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
    public void register(City city) {
        repository.save(city);
    }

    @Override
    @Transactional
    public void registerDetail(CityGenerateRequest request) {
        City city = repository.findByPlaceId(request.placeId()).orElseThrow(() -> new IllegalStateException(""));

        // TODO 트랜잭션 내에 AI 호출이 병목을 일으키진 않을지 검토 필요
        CityImageDetail cityImageDetail = cityDetailGenerator.generateDetail(city);

        city.setDetails(cityImageDetail.description(), cityImageDetail.id());
        city.setReady();
        repository.save(city);
    }
}
