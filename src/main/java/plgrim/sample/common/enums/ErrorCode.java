package plgrim.sample.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    /**
     * 400 BAD REQUEST : 잘못된 요청, Validation 에러
     * */
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Validation Error"),
    VALIDATION_ERROR_ID_EMPTY(HttpStatus.BAD_REQUEST, "이메일을 입력해주세요."),
    VALIDATION_ERROR_ID(HttpStatus.BAD_REQUEST, "이메일 형식을 확인해주세요."),
    VALIDATION_ERROR_PASSWORD_EMPTY(HttpStatus.BAD_REQUEST, "비밀번호를 입력해주세요."),
    VALIDATION_ERROR_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호는 5자 ~ 20자 사이로 입력해주세요."),
    
    /**
     * 404 NOT_FOUND : RESOURCE를 찾을 수 없음
     * */
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저 정보를 찾을 수 없습니다."),

    /**
     * 409 CONFICT : 중복된 데이터가 존재
     * */
    DUPLICATE_ID(HttpStatus.CONFLICT, "이미 중복된 회원이 존재합니다."),
    DUPLICATE_PHONE_NUMBER(HttpStatus.CONFLICT, "이미 중복된 전화번호가 존재합니다."),
    NOT_CHANGED_ID(HttpStatus.CONFLICT, "이메일이 수정정보와 이미 동일합니다."),
    NOT_CHANGED_PHONE_NUMBER(HttpStatus.CONFLICT, "전화번호가 수정정보와 이미 동일합니다.");

    private final HttpStatus httpStatus;
    private final String detail;
}
