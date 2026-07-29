package kko.traveldiary_api.journey.adaptor.inbound.internal;

import kko.traveldiary_api.journey.application.provided.CityVisitManager;
import kko.traveldiary_api.post.application.required.CityVisitAccessPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CityVisitAccessAdaptor implements CityVisitAccessPort {
    private final CityVisitManager cityVisitManager;
    @Override
    public Optional<Long> findOwnerIdOfCityVisit(Long cityVisitId) {
        return cityVisitManager.findOwnerIdOfCityVisit(cityVisitId);
    }
}
