package kko.traveldiary_api.post.adaptor.inbound.controller.dto.request;

import jakarta.validation.constraints.NotBlank;

import javax.validation.constraints.*;
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
        @Max(value = 4000, message = "Must be {max} characters or less.")
        String contents
) { }
