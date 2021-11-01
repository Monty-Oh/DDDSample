package plgrim.sample.member.domain.service;

import plgrim.sample.common.enums.Sns;

public interface SnsStrategy {

    Sns getSns();

    /**
     * code 를 사용해 토큰 요청
     */
    Object getToken(String url, String code);

    /**
     * token 검증 요청
     */
    Object validateToken(String url, String token);

    /**
     * token 을 사용해서
     * user 정보 조회
     */
    Object getUserInfo(String url, String token);
}
