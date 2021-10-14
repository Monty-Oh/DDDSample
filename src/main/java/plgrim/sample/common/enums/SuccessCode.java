package plgrim.sample.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode {
    /**
     * 200 OK   :   요청 성공
     * */
    DELETE_SUCCESS(HttpStatus.OK, "회원 삭제가 완료되었습니다.");

    private final HttpStatus httpStatus;
    private final String detail;
}
