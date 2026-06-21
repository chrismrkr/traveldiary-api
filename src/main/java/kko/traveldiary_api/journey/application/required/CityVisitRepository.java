package kko.traveldiary_api.journey.application.required;

import kko.traveldiary_api.journey.domain.CityVisit;

import java.util.Optional;

public interface CityVisitRepository {
    Optional<CityVisit> findCityVisitByIdWithJourney(Long cityVisitId);
    CityVisit save(CityVisit cityVisit);
    void deleteCityVisit(Long cityVisitId);
}
