package kko.traveldiary_api.post.application.required;

import java.util.Optional;

public interface CityVisitAccessPort {
    Optional<Long> findOwnerIdOfCityVisit(Long cityVisitId);
}
