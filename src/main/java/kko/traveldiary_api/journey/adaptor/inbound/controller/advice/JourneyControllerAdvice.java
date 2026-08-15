package kko.traveldiary_api.journey.adaptor.inbound.controller.advice;

import kko.traveldiary_api.journey.adaptor.inbound.controller.JourneyController;
import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.response.ErrorResponse;
import kko.traveldiary_api.journey.adaptor.inbound.controller.dto.response.JourneyResponseStatuses;
import kko.traveldiary_api.journey.application.exception.InvalidJourneyDateChangeException;
import kko.traveldiary_api.journey.application.exception.JourneyAccessDeniedException;
import kko.traveldiary_api.journey.application.exception.JourneyNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice(assignableTypes = {JourneyController.class})
public class JourneyControllerAdvice {
    @ExceptionHandler(exception = InvalidJourneyDateChangeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleInvalidJourneyDateChange(InvalidJourneyDateChangeException exception) {
        ErrorResponse errorResponse = new ErrorResponse(JourneyResponseStatuses.INVALID_JOURNEY_DATE_CHANGE, exception.getMessage(), null);
        return ResponseEntity.badRequest()
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

    @ExceptionHandler(exception = JourneyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleInvalidJourneyNotFound(JourneyNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(exception = JourneyAccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleJourneyAccessDenied(JourneyAccessDeniedException exception) {
        ErrorResponse errorResponse = new ErrorResponse(JourneyResponseStatuses.NOT_OWNED_JOURNEY_ACCESS, exception.getMessage(), null);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(errorResponse);
    }

    @ExceptionHandler(exception = HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException exception) {
        // 파싱 단계에서 실패한 요청(잘못된 enum 값, 깨진 JSON 등)도 500이 아닌 400으로 돌려준다.
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(JourneyResponseStatuses.INVALID_PARAM, "Request body is malformed.", null));
    }

    @ExceptionHandler(exception = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleUnknownError(Exception exception) {
        log.error("[Unknown Error]", exception);
        return ResponseEntity.internalServerError()
                .body(new ErrorResponse(JourneyResponseStatuses.UNKNOWN_ERROR, "", null));
    }

}
