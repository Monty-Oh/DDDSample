package plgrim.sample.common.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import plgrim.sample.common.enums.ErrorCode;

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

    /**
     * ValidationException
     * */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> custom(MethodArgumentNotValidException exception) {
        // body에 메시지 설정
        return ResponseEntity.status(ErrorCode.VALIDATION_ERROR.getHttpStatus())
                .body(exception.getBindingResult()
                        .getAllErrors()
                        .get(0)
                        .getDefaultMessage());
    }
}
