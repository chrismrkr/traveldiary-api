package kko.traveldiary_api.city.adaptor.event;

import kko.traveldiary_api.city.application.required.CityRegistrationEventPublisher;
import kko.traveldiary_api.city.domain.CityGenerateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CityRegisterSpringEventPublisher implements CityRegistrationEventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(CityGenerateRequest request) {
        eventPublisher.publishEvent(request);
    }
}
