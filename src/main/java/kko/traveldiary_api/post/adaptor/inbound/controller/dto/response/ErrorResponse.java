package kko.traveldiary_api.post.adaptor.inbound.controller.dto.response;

import java.util.List;

public record ErrorResponse(PostResponseStatuses status,
                            String message,
                            List<FieldError> errors) {

    public record FieldError(String field, String message, Object rejectedValue) {}
}
