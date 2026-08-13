package kko.traveldiary_api.journey.adaptor.inbound.controller.dto.request;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public record CityVisitModifyReqDto(
        @NotNull
        Long journeyId,

        @NotNull
        Long cityVisitId,

        @NotNull
        LocalDate startDate,

        @NotNull
        LocalDate endDate) {
}
