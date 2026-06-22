package kko.traveldiary_api.journey.application;

import kko.traveldiary_api.journey.application.provided.CityVisitManager;
import kko.traveldiary_api.journey.application.required.CityQueryPort;
import kko.traveldiary_api.journey.application.required.CityVisitRepository;
import kko.traveldiary_api.journey.application.required.JourneyRepository;
import kko.traveldiary_api.journey.domain.CityVisit;
import kko.traveldiary_api.journey.domain.Journey;
import kko.traveldiary_api.shared.Coordinate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CityVisitService implements CityVisitManager {
    private final JourneyRepository journeyRepository;
    private final CityVisitRepository cityVisitRepository;
    private final CityQueryPort cityQueryPort;

    @Override
    @Transactional
    public CityVisit visit(Long journeyId, String cityName, String cityId, Coordinate coordinate,
                           LocalDate startDate, LocalDate endDate) {
        Journey journey = journeyRepository.findById(journeyId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Journey Id: Not Found"));
        CityQueryPort.CityInfo cityInfo = cityQueryPort.search(cityName, cityId, coordinate);

        CityVisit cityVisit = CityVisit.builder()
                .journey(journey).cityId(cityInfo.cityId())
                .startDate(startDate).endDate(endDate)
                .build();
        cityVisit.validateVisitedDate(startDate, endDate);

        return cityVisitRepository.save(cityVisit);
    }

    @Override
    @Transactional
    public CityVisit changeDate(Long cityVisitId, LocalDate startDate, LocalDate endDate) {
        CityVisit cityVisit = cityVisitRepository.findCityVisitByIdWithJourney(cityVisitId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid CityVisit Id: Not Found"));
        cityVisit.changeStartDate(startDate);
        cityVisit.changeEndDate(endDate);
        return cityVisitRepository.save(cityVisit);
    }

    @Override
    public void delete(Long cityVisitId) {
        cityVisitRepository.deleteCityVisit(cityVisitId);
    }

}
