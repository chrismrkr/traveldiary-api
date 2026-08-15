package kko.traveldiary_api.journey.adaptor.inbound.controller.dto.request;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kko.traveldiary_api.journey.domain.Visibility;

import java.time.LocalDate;


public record JourneyRegisterReqDto(
        @NotNull
        LocalDate startDate,

        @NotNull
        LocalDate endDate,

        @NotBlank
        @Size(max = 20, message = "Journey name is too long (max {max} characters).")
        String name,

        @NotNull
        Visibility visibility) {

}
