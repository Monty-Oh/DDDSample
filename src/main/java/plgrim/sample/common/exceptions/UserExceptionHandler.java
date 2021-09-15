package plgrim.sample.common.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserExceptionHandler {
    /**
     * UserException
     */
    @ExceptionHandler(UserException.class)
    public ResponseEntity<String> custom(UserException exception) {
        return ResponseEntity.status(exception.getErrorCode().getHttpStatus())
                .body(exception.getErrorCode().getDetail());
    }
}
