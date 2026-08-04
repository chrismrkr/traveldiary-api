package kko.traveldiary_api.city.adaptor.inbound.controller.advice;

import kko.traveldiary_api.city.adaptor.inbound.controller.CityController;
import kko.traveldiary_api.city.adaptor.inbound.controller.dto.response.CommonCityResponse;
import kko.traveldiary_api.city.adaptor.inbound.controller.dto.response.CityResponseStatuses;
import kko.traveldiary_api.city.domain.CityImageNotFoundException;
import kko.traveldiary_api.city.domain.CityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(assignableTypes = {CityController.class})
public class CityControllerAdvice {
    @ExceptionHandler(exception = CityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<CommonCityResponse<Object>> handleCityNotFound(CityNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(exception = CityImageNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<CommonCityResponse<Object>> handleCityImageNotFound(CityImageNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(exception = Exception.class)
    public ResponseEntity<CommonCityResponse<Object>> handleUnknownError(Exception exception) {
        log.error("[Unknown Error]", exception);
        return ResponseEntity.internalServerError()
                .body(new CommonCityResponse<>(CityResponseStatuses.UNKNOWN_ERROR, ""));
    }
}
