package plgrim.sample.common.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class UserExceptionHandler extends ResponseEntityExceptionHandler {
    /**
     * UserException
     */
    @ExceptionHandler({UserException.class})
    public ResponseEntity<String> custom(UserException exception) {
        return ResponseEntity.status(exception.getErrorCode().getHttpStatus())
                .body(exception.getErrorCode().getDetail());
    }

    //  Parameter, Param
    @ExceptionHandler(ConstraintViolationException.class)
    public Object exception(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    /**
     * ValidationException
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(
                ex,
                ex.getBindingResult()
                        .getAllErrors()
                        .get(0)
                        .getDefaultMessage(),
                headers,
                status,
                request);
    }
}
