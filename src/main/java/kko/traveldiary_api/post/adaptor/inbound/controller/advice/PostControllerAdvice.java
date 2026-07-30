package kko.traveldiary_api.post.adaptor.inbound.controller.advice;

import kko.traveldiary_api.post.adaptor.inbound.controller.PostController;
import kko.traveldiary_api.post.adaptor.inbound.controller.dto.response.CommonPostResponse;
import kko.traveldiary_api.post.adaptor.inbound.controller.dto.response.PostResponseStatuses;
import kko.traveldiary_api.post.application.exception.PostAccessDeniedException;
import kko.traveldiary_api.post.application.exception.PostCityVisitNotFoundException;
import kko.traveldiary_api.post.application.exception.PostNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(assignableTypes = {PostController.class})
public class PostControllerAdvice {
    @ExceptionHandler(exception = PostNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<CommonPostResponse<Object>> handlePostNotFound(PostNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(exception = PostCityVisitNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<CommonPostResponse<Object>> handleCityVisitNotFound(PostCityVisitNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(exception = PostAccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<CommonPostResponse<Object>> handlePostAccessDenied(PostAccessDeniedException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new CommonPostResponse<>(PostResponseStatuses.NOT_OWNED_POST_ACCESS, exception.getMessage()));
    }

    @ExceptionHandler(exception = Exception.class)
    public ResponseEntity<CommonPostResponse<Object>> handleUnknownError(Exception exception) {
        log.error("[Unknown Error]", exception);
        return ResponseEntity.internalServerError()
                .body(new CommonPostResponse<>(PostResponseStatuses.UNKNOWN_ERROR, ""));
    }
}
