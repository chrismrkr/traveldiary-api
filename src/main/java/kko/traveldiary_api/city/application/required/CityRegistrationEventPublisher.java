package kko.traveldiary_api.city.application.required;

import kko.traveldiary_api.city.domain.CityGenerateRequest;
import kko.traveldiary_api.shared.Coordinate;

public interface CityRegistrationEventPublisher {
    void publish(CityGenerateRequest request);
}
