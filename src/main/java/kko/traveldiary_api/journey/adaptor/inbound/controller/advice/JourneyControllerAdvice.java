package kko.traveldiary_api.journey.adaptor.inbound.controller.advice;

import kko.traveldiary_api.journey.adaptor.inbound.controller.JourneyController;
import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.response.CommonJourneyResponse;
import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.response.JourneyResponseStatuses;
import kko.traveldiary_api.journey.application.exception.InvalidJourneyDateChangeException;
import kko.traveldiary_api.journey.application.exception.JourneyAccessDeniedException;
import kko.traveldiary_api.journey.application.exception.JourneyNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(assignableTypes = {JourneyController.class})
public class JourneyControllerAdvice {
    @ExceptionHandler(exception = InvalidJourneyDateChangeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<CommonJourneyResponse<Object>> handleInvalidJourneyDateChange(InvalidJourneyDateChangeException exception) {
        return ResponseEntity.badRequest()
                .body(new CommonJourneyResponse<>(JourneyResponseStatuses.INVALID_JOURNEY_DATE_CHANGE, exception.getMessage()));
    }

    @ExceptionHandler(exception = JourneyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<CommonJourneyResponse<Object>> handleInvalidJourneyNotFound(JourneyNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(exception = JourneyAccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<CommonJourneyResponse<Object>> handleJourneyAccessDenied(JourneyAccessDeniedException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new CommonJourneyResponse<>(JourneyResponseStatuses.NOT_OWNED_JOURNEY_ACCESS, exception.getMessage()));
    }

    @ExceptionHandler(exception = Exception.class)
    public ResponseEntity<CommonJourneyResponse<Object>> handleUnknownError(Exception exception) {
        log.error("[Unknown Error]", exception);
        return ResponseEntity.internalServerError()
                .body(new CommonJourneyResponse<>(JourneyResponseStatuses.UNKNOWN_ERROR, ""));
    }

}
