package kko.traveldiary_api.journey.adaptor.inbound.controller.advice;

import kko.traveldiary_api.journey.adaptor.inbound.controller.CityVisitController;
import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.response.CommonResponse;
import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.response.ResponseStatuses;
import kko.traveldiary_api.journey.application.exception.CityVisitNotFoundException;
import kko.traveldiary_api.journey.application.exception.InvalidCityVisitDateChange;
import kko.traveldiary_api.journey.application.exception.InvalidCityVisitOrderException;
import kko.traveldiary_api.journey.application.exception.JourneyAccessDeniedException;
import kko.traveldiary_api.journey.application.exception.JourneyNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(assignableTypes = {CityVisitController.class})
public class CityVisitControllerAdvice {

    @ExceptionHandler(exception = JourneyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<CommonResponse<Object>> handleJourneyNotFound(JourneyNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(exception = CityVisitNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<CommonResponse<Object>> handleCityVisitNotFound(CityVisitNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(exception = JourneyAccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<CommonResponse<Object>> handleJourneyAccessDenied(JourneyAccessDeniedException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new CommonResponse<>(ResponseStatuses.NOT_OWNED_JOURNEY_ACCESS, exception.getMessage()));
    }

    @ExceptionHandler(exception = InvalidCityVisitDateChange.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<CommonResponse<Object>> handleInvalidCityVisitDateChange(InvalidCityVisitDateChange exception) {
        return ResponseEntity.badRequest()
                .body(new CommonResponse<>(ResponseStatuses.INVALID_CITY_VISIT_DATE_CHANGE, exception.getMessage()));
    }

    @ExceptionHandler(exception = InvalidCityVisitOrderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<CommonResponse<Object>> handleInvalidCityVisitOrder(InvalidCityVisitOrderException exception) {
        return ResponseEntity.badRequest()
                .body(new CommonResponse<>(ResponseStatuses.INVALID_CITY_VISIT_ORDER, exception.getMessage()));
    }

    @ExceptionHandler(exception = Exception.class)
    public ResponseEntity<CommonResponse<Object>> handleUnknownError(Exception exception) {
        log.error("[Unknown Error]", exception);
        return ResponseEntity.internalServerError()
                .body(new CommonResponse<>(ResponseStatuses.UNKNOWN_ERROR, ""));
    }
}
