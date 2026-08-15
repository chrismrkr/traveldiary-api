package kko.traveldiary_api.post.adaptor.inbound.controller.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PostAttachReqDto(
        @NotNull
        Long cityVisitId,

        String placeName, String provider, String placeId,

        @DecimalMin("-90.0") @DecimalMax("90.0")
        @Digits(integer = 2, fraction = 4)
        BigDecimal latitude,

        @DecimalMin("-180.0") @DecimalMax("180.0")
        @Digits(integer = 3, fraction = 4)
        BigDecimal longitude,

        @NotBlank
        @Size(max = 4000, message = "Must be {max} characters or less.")
        String contents
) { }
