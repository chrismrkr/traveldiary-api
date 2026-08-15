package kko.traveldiary_api.journey.adaptor.inbound.controller.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import kko.traveldiary_api.journey.domain.CityVisitOrder;

import java.util.List;

public record CityVisitOrderRealignReqDto(
        @NotNull
        Long journeyId,

        @Valid
        @NotEmpty
        List<CityVisitOrder> orders) {
}
