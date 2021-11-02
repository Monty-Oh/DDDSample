package plgrim.sample.common.token;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.member.domain.service.SnsStrategy;
import plgrim.sample.member.domain.service.SnsStrategyFactory;
import plgrim.sample.member.infrastructure.rest.dto.KakaoTokenDTO;
import plgrim.sample.member.infrastructure.rest.dto.KakaoUserInfoDTO;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import static plgrim.sample.common.KakaoValue.*;

@RequiredArgsConstructor
@Component
public class KakaoTokenProvider {
    private final UserDetailsService userDetailsService;
    private final SnsStrategyFactory snsStrategyFactory;

    private SnsStrategy snsStrategy;

    @PostConstruct
    public void init() {
        snsStrategy = snsStrategyFactory.findSnsStrategy(Sns.KAKAO);
    }

    /**
     * 로그인 시도 시 받은 인가 코드를 매개변수로 넘긴다.
     * 토큰 2가지와 만료시간 등이 적혀있다.
     */
    public KakaoTokenDTO createToken(String code) {
        return (KakaoTokenDTO) snsStrategy.getToken(KAPI_GET_TOKEN_URL, code);
    }

    /**
     * userDetailService 에서 사용자 정보를 조회해서 넘긴다.
     * 카카오로부터 토큰을 사용
     */
    public Authentication getAuthentication(String token) {
        String userPk = this.getUserPk(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userPk);
        return new UsernamePasswordAuthenticationToken(userDetailsService.loadUserByUsername(userPk), "", userDetails.getAuthorities());
    }

    public String getUserPk(String token) {
        return ((KakaoUserInfoDTO) snsStrategy.getUserInfo(KAPI_USER_INFO_URL, token)).getId().toString();
    }

    /**
     * Request 의 Header 에서 token 값을 가져온다. "X-AUTH-TOKEN" : "TOKEN 값'
     */
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("X-AUTH-TOKEN");
    }

    /**
     * Request 의 Header 에서 SnsType 값을 가져온다. "X-SNS-TYPE"   :   "SnsType 값"
     */
    public String resolveSnsType(HttpServletRequest request) {
        return request.getHeader("X-SNS-TYPE");
    }

    /**
     * 해당 토큰에 대한 검증을 실시한다.
     */
    public boolean validateToken(String jwtToken) {
        try {
            snsStrategy.validateToken(KAPI_CHECK_ACCESS_TOKEN_URL, jwtToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
