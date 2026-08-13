package kko.traveldiary_api.post.adaptor.inbound.controller.dto.response;

import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.response.JourneyResponseStatuses;

import java.util.List;

public record ErrorResponse(JourneyResponseStatuses status,
                            String message,
                            List<kko.traveldiary_api.journey.adaptor.inbound.controller.dto.response.ErrorResponse.FieldError> errors) {



    public record FieldError(String field, String message, Object rejectedValue) {}
}