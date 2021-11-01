package plgrim.sample.common.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import plgrim.sample.common.enums.ErrorCode;

@Getter
@AllArgsConstructor
public class UserException extends RuntimeException {
    private final ErrorCode errorCode;
}
