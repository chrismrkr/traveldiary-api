package kko.traveldiary_api.journey.adaptor.inbound.controller.advice;

import kko.traveldiary_api.journey.adaptor.inbound.controller.CityVisitController;
import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.response.CommonJourneyResponse;
import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.response.ErrorResponse;
import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.response.JourneyResponseStatuses;
import kko.traveldiary_api.journey.application.exception.CityVisitNotFoundException;
import kko.traveldiary_api.journey.application.exception.InvalidCityVisitDateChange;
import kko.traveldiary_api.journey.application.exception.InvalidCityVisitOrderException;
import kko.traveldiary_api.journey.application.exception.JourneyAccessDeniedException;
import kko.traveldiary_api.journey.application.exception.JourneyNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice(assignableTypes = {CityVisitController.class})
public class CityVisitControllerAdvice {

    @ExceptionHandler(exception = JourneyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleJourneyNotFound(JourneyNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(exception = CityVisitNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleCityVisitNotFound(CityVisitNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(exception = JourneyAccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleJourneyAccessDenied(JourneyAccessDeniedException exception) {
        ErrorResponse errorResponse = new ErrorResponse(JourneyResponseStatuses.NOT_OWNED_JOURNEY_ACCESS, exception.getMessage(), null);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(errorResponse);
    }

    @ExceptionHandler(exception = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleInvalidMethodParam(MethodArgumentNotValidException exception) {
        List<ErrorResponse.FieldError> fieldErrors = exception.getBindingResult().getFieldErrors().stream()
                .map(err -> new ErrorResponse.FieldError(
                        err.getField(),
                        err.getDefaultMessage(),
                        err.getRejectedValue()
                ))
                .toList();
        ErrorResponse errorResponse = new ErrorResponse(JourneyResponseStatuses.INVALID_PARAM, "", fieldErrors);
        return ResponseEntity.badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler(exception = InvalidCityVisitDateChange.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleInvalidCityVisitDateChange(InvalidCityVisitDateChange exception) {
        ErrorResponse errorResponse = new ErrorResponse(JourneyResponseStatuses.INVALID_CITY_VISIT_DATE_CHANGE, exception.getMessage(), null);
        return ResponseEntity.badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler(exception = InvalidCityVisitOrderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleInvalidCityVisitOrder(InvalidCityVisitOrderException exception) {
        ErrorResponse errorResponse = new ErrorResponse(JourneyResponseStatuses.INVALID_CITY_VISIT_ORDER, exception.getMessage(), null);
        return ResponseEntity.badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler(exception = Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknownError(Exception exception) {
        log.error("[Unknown Error]", exception);
        exception.printStackTrace();
        return ResponseEntity.internalServerError()
                .body(new ErrorResponse(JourneyResponseStatuses.UNKNOWN_ERROR, "", null));
    }
}
