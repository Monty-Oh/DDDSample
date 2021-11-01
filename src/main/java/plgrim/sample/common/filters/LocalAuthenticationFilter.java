package plgrim.sample.common.filters;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.common.token.LocalTokenProvider;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequiredArgsConstructor
public class LocalAuthenticationFilter extends GenericFilterBean {

    private final LocalTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String snsType = jwtTokenProvider.resolveSnsType((HttpServletRequest) request);
        //  SNS 타입이 Local 일 때 수행한다.
        if (snsType != null && snsType.equals(Sns.LOCAL.getValue())) {
            //  헤터에서 JWT 를 받아온다.
            String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);
            //  유효한 토큰인지 확인한다.
            if (token != null && jwtTokenProvider.validateToken(token)) {
                //  토큰이 유효하면 토큰으로부터 유저 정보를 받아온다.
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                //  SecurityContext 에 Authentication 객체를 저장한다.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }
}
