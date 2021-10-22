package plgrim.sample;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import plgrim.sample.common.KakaoTokenProvider;
import plgrim.sample.common.LocalTokenProvider;
import plgrim.sample.common.exceptions.AuthenticationEntryPointImpl;
import plgrim.sample.common.filters.KakaoAuthenticationFilter;
import plgrim.sample.common.filters.LocalAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final LocalTokenProvider jwtTokenProvider;
    private final KakaoTokenProvider kakaoTokenProvider;
//    private final KakaoRestApiService kakaoRestApiService;

    //  암호화에 필요한 PasswordEncoder 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    //  authenticationManager 등록
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()  // 기본 설정 해제
                .csrf().disable()       //  csrf 보안토큰 처리 해제
                //  세션 사용하지 않음. (토큰 인증 기반)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()    //  요청에 대한 사용 권한 체크
                .antMatchers("/users/**").hasRole("USER")
                .antMatchers("/login/**").permitAll()   //  로그인 요청은 누구나 접근 가능
                .anyRequest().denyAll() //  현재 개발한 경로 외에는 모두 접근 금지
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPointImpl())
                .and()
                //  JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 전에 넣는다.
                .addFilterBefore(new LocalAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new KakaoAuthenticationFilter(kakaoTokenProvider), UsernamePasswordAuthenticationFilter.class);
    }
}
