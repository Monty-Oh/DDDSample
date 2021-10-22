package plgrim.sample.common;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import plgrim.sample.member.controller.dto.user.KakaoTokenDTO;
import plgrim.sample.member.infrastructure.rest.KakaoRestApiService;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@Component
public class KakaoTokenProvider {
    private final UserDetailsService userDetailsService;
    private final KakaoRestApiService kakaoRestApiService;

    //  로그인 시도 시 받은 인가 코드를 매개변수로 넘긴다.
    //  토큰 2가지와 만료시간 등이 적혀있다.
    public KakaoTokenDTO createToken(String code) {
        return kakaoRestApiService.getKakaoLoginTokenUsingAuthCode(code);
    }

    //  userDetailService 에서 사용자 정보를 조회해서 넘긴다.
    public Authentication getAuthentication(String token) {
        //  kakao api 로부터 조회한다.
        String userPk = kakaoRestApiService.getKakaoUserInfo(token);
        //  찾은 userPk로 조회한다.
        UserDetails userDetails = userDetailsService.loadUserByUsername(userPk);
        //  새로운 인증용 객체를 반환한다.
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
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
            kakaoRestApiService.validateToken(jwtToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
