package plgrim.sample.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    /**
     * 400 BAD REQUEST : 잘못된 요청
     * */
    
    
    /**
     * 404 NOT_FOUND : RESOURCE를 찾을 수 없음
     * */
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저 정보를 찾을 수 없습니다."),

    /**
     * 409 CONFICT : 중복된 데이터가 존재
     * */
    DUPLICATE_ID(HttpStatus.CONFLICT, "이미 중복된 회원이 존재합니다."),
    DUPLICATE_PHONE_NUMBER(HttpStatus.CONFLICT, "이미 중복된 전화번호가 존재합니다.");

    private final HttpStatus httpStatus;
    private final String detail;
}
