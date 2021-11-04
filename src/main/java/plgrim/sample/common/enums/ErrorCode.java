package plgrim.sample.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    /**
     * 400 BAD REQUEST  :   잘못된 요청, Validation 에러
     */
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Validation Error"),
    VALIDATION_ERROR_PHONE_NUMBER_EMPTY(HttpStatus.BAD_REQUEST, "전화번호를 입력해주세요."),
    VALIDATION_ERROR_PHONE_NUMBER(HttpStatus.BAD_REQUEST, "전화번호 형식을 확인해주세요."),
    VALIDATION_ERROR_ID_EMPTY(HttpStatus.BAD_REQUEST, "이메일을 입력해주세요."),
    VALIDATION_ERROR_ID(HttpStatus.BAD_REQUEST, "이메일 형식을 확인해주세요."),
    VALIDATION_ERROR_PASSWORD_EMPTY(HttpStatus.BAD_REQUEST, "비밀번호를 입력해주세요."),
    VALIDATION_ERROR_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호는 5자 ~ 20자 사이로 입력해주세요."),
    ILLEGAL_ARGUMENT_SNS(HttpStatus.BAD_REQUEST, "해당 Sns 타입을 찾을 수 없습니다."),

    /**
     * 401 SC_UNAUTHORIZED :   인증 필요, 권한 없음
     */
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "권한이 없는 요청입니다."),
    TOKEN_NOT_EXIST(HttpStatus.UNAUTHORIZED, "존재하지 않는 엑세스 토큰 입니다,"),

    /**
     * 403 FORBIDDEN    :   서버가 클라이언트의 접근을 허용하지 않음
     */
    INCORRECT_PASSWORD(HttpStatus.FORBIDDEN, "비밀번호가 일치하지 않습니다."),
    EXPIRED_TOKEN(HttpStatus.FORBIDDEN, "토큰이 만료되었습니다."),

    /**
     * 404 NOT_FOUND    :   RESOURCE를 찾을 수 없음
     */
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저 정보를 찾을 수 없습니다."),

    /**
     * 409 CONFLICT     :   중복된 데이터가 존재
     */
    DUPLICATE_USER_ID(HttpStatus.CONFLICT, "이미 중복된 아이디가 존재합니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 중복된 이메일이 존재합니다."),
    DUPLICATE_MOBILE_NO(HttpStatus.CONFLICT, "이미 중복된 전화번호가 존재합니다."),
    NOT_CHANGED_ID(HttpStatus.CONFLICT, "이메일이 수정정보와 이미 동일합니다."),
    NOT_CHANGED_PHONE_NUMBER(HttpStatus.CONFLICT, "전화번호가 수정정보와 이미 동일합니다."),

    /**
     * 500 Server Error :   서버 에러
     */
    API_SERVER(HttpStatus.INTERNAL_SERVER_ERROR, "외부 API 서버에 문제가 생겼습니다.");

    private final HttpStatus httpStatus;
    private final String detail;
}
