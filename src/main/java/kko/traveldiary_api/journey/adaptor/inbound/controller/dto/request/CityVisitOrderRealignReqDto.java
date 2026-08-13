package kko.traveldiary_api.journey.adaptor.inbound.controller.dto.request;

import kko.traveldiary_api.journey.domain.CityVisitOrder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public record CityVisitOrderRealignReqDto(
        @NotNull
        Long journeyId,
        @Valid
        List<CityVisitOrder> orders) {
}
