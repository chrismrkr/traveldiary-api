package kko.traveldiary_api.journey.adaptor.inbound.internal;

import kko.traveldiary_api.journey.application.provided.CityVisitManager;
import kko.traveldiary_api.post.application.required.CityVisitQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CityVisitQueryAdaptor implements CityVisitQueryPort {
    private final CityVisitManager cityVisitManager;
    @Override
    public Long findOwnerIdfCityVisit(Long cityVisitId) {
        return null;
    }
}
