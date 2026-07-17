package kko.traveldiary_api.journey.adaptor.controller.advice;

import kko.traveldiary_api.journey.adaptor.controller.JourneyController;
import kko.traveldiary_api.journey.adaptor.controller.dto.response.CommonResponse;
import kko.traveldiary_api.journey.adaptor.controller.dto.response.ResponseStatuses;
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
    public ResponseEntity<CommonResponse<Object>> handleInvalidJourneyDateChange(InvalidJourneyDateChangeException exception) {
        return ResponseEntity.badRequest()
                .body(new CommonResponse<>(ResponseStatuses.INVALID_JOURNEY_DATE_CHANGE, exception.getMessage()));
    }

    @ExceptionHandler(exception = JourneyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<CommonResponse<Object>> handleInvalidJourneyNotFound(JourneyNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(exception = JourneyAccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<CommonResponse<Object>> handleJourneyAccessDenied(JourneyAccessDeniedException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new CommonResponse<>(ResponseStatuses.NOT_OWNED_JOURNEY_ACCESS, exception.getMessage()));
    }

    @ExceptionHandler(exception = Exception.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ResponseEntity<Object> handleUnknownError(JourneyAccessDeniedException exception) {
        exception.fillInStackTrace();
        log.error("[Unknown Error] {}", exception.getMessage());
        return ResponseEntity.internalServerError()
                .body(new CommonResponse<>(ResponseStatuses.UNKNOWN_ERROR, ""));
    }

}
