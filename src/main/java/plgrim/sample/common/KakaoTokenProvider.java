package plgrim.sample.common;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import plgrim.sample.member.infrastructure.rest.KakaoRestApiService;
import plgrim.sample.member.infrastructure.rest.dto.KakaoTokenDTO;

import javax.servlet.http.HttpServletRequest;

import static plgrim.sample.common.KakaoValue.*;

@RequiredArgsConstructor
@Component
public class KakaoTokenProvider {
    private final UserDetailsService userDetailsService;
    private final KakaoRestApiService kakaoRestApiService;

    //  로그인 시도 시 받은 인가 코드를 매개변수로 넘긴다.
    //  토큰 2가지와 만료시간 등이 적혀있다.
    public KakaoTokenDTO createToken(String code) {
        return kakaoRestApiService.getKakaoAccessTokensUsingAuthCode(KAPI_GET_TOKEN_URL, code);
    }

    //  userDetailService 에서 사용자 정보를 조회해서 넘긴다.
    //  카카오로부터 토크을 사용
    public Authentication getAuthentication(String token) {
        String userPk = kakaoRestApiService.getKakaoUserInfo(KAPI_USER_INFO_URL, token).getId().toString();
        UserDetails userDetails = userDetailsService.loadUserByUsername(userPk);
        return new UsernamePasswordAuthenticationToken( userDetailsService.loadUserByUsername(userPk), "", userDetails.getAuthorities());
    }

    //  Request 의 Header 에서 token 값을 가져옵니다. "X-AUTH-TOKEN" : "TOKEN 값'
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("X-AUTH-TOKEN");
    }

    //  Request 의 Header 에서 SnsType 값을 가져온다. "X-SNS-TYPE"   :   "SnsType 값"
    public String resolveSnsType(HttpServletRequest request) {
        return request.getHeader("X-SNS-TYPE");
    }

    public boolean validateToken(String jwtToken) {
        try {
            kakaoRestApiService.validateToken(KAPI_CHECK_ACCESS_TOKEN_URL, jwtToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
