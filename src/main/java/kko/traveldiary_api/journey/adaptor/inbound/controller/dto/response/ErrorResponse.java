package kko.traveldiary_api.journey.adaptor.inbound.controller.dto.response;

import java.util.List;

public record ErrorResponse(JourneyResponseStatuses status,
                            String message,
                            List<FieldError> errors) {



    public record FieldError(String field, String message, Object rejectedValue) {}
}
