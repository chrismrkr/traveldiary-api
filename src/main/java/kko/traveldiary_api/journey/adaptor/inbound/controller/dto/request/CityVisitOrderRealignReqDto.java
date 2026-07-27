package kko.traveldiary_api.journey.adaptor.inbound.controller.dto.request;

import kko.traveldiary_api.journey.domain.CityVisitOrder;

import java.util.List;

public record CityVisitOrderRealignReqDto(Long journeyId,
                                          List<CityVisitOrder> orders) {
}
