package kko.traveldiary_api.journey.application.provided;

import kko.traveldiary_api.journey.domain.CityVisit;
import kko.traveldiary_api.shared.Coordinate;

import java.time.LocalDate;

public interface CityVisitManager {
    CityVisit visit(Long journeyId, String cityName, String cityId, Coordinate coordinate,
                    LocalDate startDate, LocalDate endDate);
    CityVisit changeDate(Long cityVisitId, LocalDate startDate, LocalDate endDate);
    void delete(Long cityVisitId);

}
