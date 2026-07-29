package kko.traveldiary_api.journey.application;

import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.request.CityVisitModifyReqDto;
import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.request.CityVisitOrderRealignReqDto;
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
        Journey journey = findJourneyAndValidateOwner(memberId, journeyId, true);

        CityQueryPort.CityInfo cityInfo = cityQueryPort.search(cityName, cityId, coordinate);

        CityVisit cityVisit = CityVisit.builder()
                .journeyId(journey.getId()).cityId(cityInfo.cityId())
                .startDate(startDate).endDate(endDate)
                .build();
        cityVisit.validateVisitedDate(endDate, journey.getEndDate());
        journey.visit(cityVisit);

        return cityVisitRepository.save(cityVisit);
    }

    @Override
    public CityVisit modify(Long memberId, CityVisitModifyReqDto reqDto) {
        Journey journey = findJourneyAndValidateOwner(memberId, reqDto.journeyId(), true);
        CityVisit cityVisit = journey.findCityVisitById(reqDto.cityVisitId())
                .orElseThrow(() -> new CityVisitNotFoundException(reqDto.cityVisitId()));

        if(reqDto.startDate() != null && reqDto.endDate() != null) {
            cityVisit.changeStartDate(reqDto.startDate(), journey.getStartDate());
            cityVisit.changeEndDate(reqDto.endDate(), journey.getEndDate());
        }

        return cityVisitRepository.save(cityVisit);
    }

    @Override
    @Transactional
    public void realignVisitOrder(Long memberId, CityVisitOrderRealignReqDto realignReqDto) {
        Journey journey = findJourneyAndValidateOwner(memberId, realignReqDto.journeyId(), true);
        journey.realignVisitOrder(realignReqDto.orders());
        journey.getCityVisits().forEach(cityVisitRepository::save);
    }

    @Override
    public void delete(Long memberId, Long journeyId, Long cityVisitId) {
        findJourneyAndValidateOwner(memberId, journeyId, false);
        cityVisitRepository.deleteCityVisit(cityVisitId);
    }

    @Override
    public Optional<Long> findOwnerIdOfCityVisit(Long cityVisitId) {
        Optional<Long> journeyId = cityVisitRepository.findCityVisitByIdWithJourney(cityVisitId)
                .map(CityVisit::getJourneyId);
        return journeyId.flatMap(aLong -> journeyRepository.findById(aLong)
                .map(Journey::getMemberId));

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
