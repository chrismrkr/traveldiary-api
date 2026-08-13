package kko.traveldiary_api.post.adaptor.inbound.controller.dto.request;

import jakarta.validation.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

public record PostContentsModifyDto(
        @NotNull
        Long postId,

        @NotBlank
        @Max(value = 4000, message = "Must be {max} characters or less.")
        String contents
) {
}
