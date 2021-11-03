package plgrim.sample.common.filters;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.common.token.LocalTokenProvider;
import plgrim.sample.common.token.TokenProvider;
import plgrim.sample.common.token.TokenProviderFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequiredArgsConstructor
public class AuthenticationFilter extends GenericFilterBean {

    private final TokenProviderFactory tokenProviderFactory;

    /**
     * Header 에서 Sns 타입을 추출, TokenProvider 를 TokenProviderFactory 에서 받는다.
     * 해당 TokenProvider 로 로직을 수행한다.
     * */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        String snsHeader = resolveSnsType((HttpServletRequest) request);

        if(StringUtils.isNotBlank(snsHeader)) {
            TokenProvider tokenProvider = tokenProviderFactory.findTokenProvider(Sns.findSnsByValue(snsHeader));
            //  헤터에서 JWT 를 받아온다.
            String token = resolveToken((HttpServletRequest) request);
            //  유효한 토큰인지 확인한다.
            if (token != null && tokenProvider.validateToken(token)) {
                //  토큰이 유효하면 토큰으로부터 유저 정보를 받아온다.
                Authentication authentication = tokenProvider.getAuthentication(token);
                //  SecurityContext 에 Authentication 객체를 저장한다.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }

    /**
     * Request 의 Header 에서 token 값을 가져온다. "X-AUTH-TOKEN" : "TOKEN 값'
     */
    private String resolveToken(HttpServletRequest request) {
        return request.getHeader("X-AUTH-TOKEN");
    }

    /**
     * Request 의 Header 에서 SnsType 값을 가져온다. "X-SNS-TYPE"   :   "SnsType 값"
     */
    private String resolveSnsType(HttpServletRequest request) {
        return request.getHeader("X-SNS-TYPE");
    }
}
