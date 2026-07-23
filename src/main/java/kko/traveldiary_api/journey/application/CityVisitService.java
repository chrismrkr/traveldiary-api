package kko.traveldiary_api.journey.application;

import kko.traveldiary_api.journey.application.exception.CityVisitNotFoundException;
import kko.traveldiary_api.journey.application.exception.JourneyAccessDeniedException;
import kko.traveldiary_api.journey.application.exception.JourneyNotFoundException;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CityVisitService implements CityVisitManager {
    private final JourneyRepository journeyRepository;
    private final CityVisitRepository cityVisitRepository;
    private final CityQueryPort cityQueryPort;

    @Override
    public CityVisit visit(Long memberId, Long journeyId, String cityName, String cityId, Coordinate coordinate,
                           LocalDate startDate, LocalDate endDate) {
        Journey journey = findJourneyAndValidateOwner(memberId, journeyId, false);

        CityQueryPort.CityInfo cityInfo = cityQueryPort.search(cityName, cityId, coordinate);

        CityVisit cityVisit = CityVisit.builder()
                .journeyId(journey.getId()).cityId(cityInfo.cityId())
                .startDate(startDate).endDate(endDate)
                .build();
        cityVisit.validateVisitedDate(endDate, journey.getEndDate());

        return cityVisitRepository.save(cityVisit);
    }

    @Override
    public CityVisit changeDate(Long memberId, Long journeyId, Long cityVisitId, LocalDate startDate, LocalDate endDate) {
        Journey journey = findJourneyAndValidateOwner(memberId, journeyId, true);

        CityVisit cityVisit = journey.findCityVisitById(cityVisitId)
                .orElseThrow(() -> new CityVisitNotFoundException(cityVisitId));
        cityVisit.changeStartDate(startDate, journey.getStartDate());
        cityVisit.changeEndDate(endDate, journey.getEndDate());
        return cityVisitRepository.save(cityVisit);
    }

    @Override
    public void delete(Long memberId, Long journeyId, Long cityVisitId) {
        findJourneyAndValidateOwner(memberId, journeyId, true);
        cityVisitRepository.deleteCityVisit(cityVisitId);
    }

    private Journey findJourneyAndValidateOwner(Long memberId, Long journeyId, boolean withCityVisits) {
        Journey journey = withCityVisits ? journeyRepository.findByIdWithCityVisit(journeyId).orElseThrow(() -> new JourneyNotFoundException(journeyId))
                : journeyRepository.findById(journeyId).orElseThrow(() -> new JourneyNotFoundException(journeyId));
        if(!journey.isOwnedBy(memberId)) {
            throw new JourneyAccessDeniedException(memberId, journeyId);
        }
        return journey;
    }

}
