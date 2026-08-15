package kko.traveldiary_api.post.adaptor.inbound.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PostContentsModifyDto(
        @NotNull
        Long postId,

        @NotBlank
        @Size(max = 4000, message = "Must be {max} characters or less.")
        String contents
) {
}
