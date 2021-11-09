package plgrim.sample.common.token;

import org.springframework.security.core.Authentication;
import plgrim.sample.common.enums.Sns;

public interface TokenProvider {
    /**
     * 해당 Token Provider 의 목표를 가져온다.
     */
    Sns
    getThisTargetSns();

    /**
     * 인증 정보 조회 후 인증 객체 생성
     */
    Authentication
    getAuthentication(String token);

    /**
     * 토큰에서 회원정보 추출
     */
    Object
    getUserInfo(String token);

    /**
     * 토큰의 유효성 체크
     */
    boolean
    validateToken(String token);
}
