package kko.traveldiary_api.journey.adaptor.inbound.controller.dto.request;


import jakarta.validation.constraints.NotBlank;
import kko.traveldiary_api.journey.domain.Visibility;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public record JourneyPatchReqDto(
        @NotNull
        Long journeyId,

        @NotNull
        LocalDate startDate,

        @NotNull
        LocalDate endDate,

        @NotBlank
        @Max(value = 20, message = "Journey name is too long (max 20 characters).")
        String name,

        @NotNull
        Visibility visibility) { }
