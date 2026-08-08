package kko.traveldiary_api.journey.application.provided;

import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.request.CityVisitModifyReqDto;
import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.request.CityVisitOrderRealignReqDto;
import kko.traveldiary_api.journey.domain.CityVisit;
import kko.traveldiary_api.shared.Coordinate;

import java.time.LocalDate;
import java.util.Optional;

public interface CityVisitManager {
    CityVisit visit(Long memberId, Long journeyId, String cityName, String placeId, Coordinate coordinate,
                    LocalDate startDate, LocalDate endDate);
    CityVisit modify(Long memberId, CityVisitModifyReqDto modifyReqDto);
    void realignVisitOrder(Long memberId, CityVisitOrderRealignReqDto realignReqDto);
    void delete(Long memberId, Long journeyId, Long cityVisitId);
    Optional<Long> findOwnerIdOfCityVisit(Long cityVisitId);
}
