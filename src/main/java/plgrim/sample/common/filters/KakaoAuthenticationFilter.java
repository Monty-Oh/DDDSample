package plgrim.sample.common.filters;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import plgrim.sample.common.KakaoTokenProvider;
import plgrim.sample.common.enums.Sns;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequiredArgsConstructor
public class KakaoAuthenticationFilter extends GenericFilterBean {

    private final KakaoTokenProvider kakaoTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String snsType = kakaoTokenProvider.resolveSnsType((HttpServletRequest) request);
        //  Sns 타입이 Kakao 일 때 수행
        if(snsType != null && snsType.equals(Sns.KAKAO.toString().toLowerCase())) {
            //  헤더에서 JWT 를 받아온다.
            String token = kakaoTokenProvider.resolveToken((HttpServletRequest) request);
            //  유효한 토큰인지 API 를 이용해서 확인한다.
            if (token != null && kakaoTokenProvider.validateToken(token)) {
                //  토큰이 유효하면 토큰으로부터 유저 정보를 받아온다.
                Authentication authentication = kakaoTokenProvider.getAuthentication(token);
                //  SecurityContext 에 Authentication 객체를 저장한다.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(request, response);
    }
}
