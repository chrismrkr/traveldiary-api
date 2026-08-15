package kko.traveldiary_api.journey.adaptor.inbound.controller.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CityVisitCreateReqDto(
        @NotNull
        Long journeyId,

        @NotBlank
        String cityName,

        String placeId,

        @DecimalMin("-90.0") @DecimalMax("90.0")
        @Digits(integer = 2, fraction = 4)
        @NotNull
        BigDecimal latitude,

        @DecimalMin("-180.0") @DecimalMax("180.0")
        @Digits(integer = 3, fraction = 4)
        @NotNull
        BigDecimal longitude,

        @NotNull
        LocalDate startDate,

        @NotNull
        LocalDate endDate){
}
