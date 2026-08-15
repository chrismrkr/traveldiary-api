package kko.traveldiary_api.post.adaptor.inbound.controller.advice;

import kko.traveldiary_api.post.adaptor.inbound.controller.PostController;
import kko.traveldiary_api.post.adaptor.inbound.controller.dto.response.ErrorResponse;
import kko.traveldiary_api.post.adaptor.inbound.controller.dto.response.PostResponseStatuses;
import kko.traveldiary_api.post.application.exception.PostAccessDeniedException;
import kko.traveldiary_api.post.application.exception.PostCityVisitNotFoundException;
import kko.traveldiary_api.post.application.exception.PostNotFoundException;
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
@RestControllerAdvice(assignableTypes = {PostController.class})
public class PostControllerAdvice {
    @ExceptionHandler(exception = PostNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handlePostNotFound(PostNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(exception = PostCityVisitNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleCityVisitNotFound(PostCityVisitNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(exception = PostAccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handlePostAccessDenied(PostAccessDeniedException exception) {
        ErrorResponse errorResponse = new ErrorResponse(PostResponseStatuses.NOT_OWNED_POST_ACCESS, exception.getMessage(), null);
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
        ErrorResponse errorResponse = new ErrorResponse(PostResponseStatuses.INVALID_PARAM, "", fieldErrors);
        return ResponseEntity.badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler(exception = HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException exception) {
        // 파싱 단계에서 실패한 요청(깨진 JSON, 타입 불일치 등)도 500이 아닌 400으로 돌려준다.
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(PostResponseStatuses.INVALID_PARAM, "Request body is malformed.", null));
    }

    @ExceptionHandler(exception = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleUnknownError(Exception exception) {
        log.error("[Unknown Error]", exception);
        return ResponseEntity.internalServerError()
                .body(new ErrorResponse(PostResponseStatuses.UNKNOWN_ERROR, "", null));
    }
}
