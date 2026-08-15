package kko.traveldiary_api.journey.adaptor.inbound.controller.dto.request;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kko.traveldiary_api.journey.domain.Visibility;

import java.time.LocalDate;

/** PATCH 이므로 journeyId 외의 필드는 null(=변경 없음)을 허용하고, 값이 있을 때만 형식을 검증한다. */
public record JourneyPatchReqDto(
        @NotNull
        Long journeyId,

        LocalDate startDate,

        LocalDate endDate,

        @Size(max = 20, message = "Journey name is too long (max {max} characters).")
        String name,

        Visibility visibility) { }
