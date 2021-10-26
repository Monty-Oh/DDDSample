package plgrim.sample.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import plgrim.sample.common.enums.ErrorCode;
import plgrim.sample.common.enums.Gender;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.member.application.UserJoinService;
import plgrim.sample.member.domain.model.aggregates.User;
import plgrim.sample.member.domain.model.commands.UserJoinCommand;
import plgrim.sample.member.domain.model.valueobjects.UserBasic;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtTokenProvider 테스트")
@SpringBootTest
class JwtTokenProviderTest {
    @Autowired
    LocalTokenProvider jwtTokenProvider;

    @Autowired
    UserJoinService userJoinService;

    @DisplayName("HttpHeader 에서 토큰 추출")
    @Test
    void resolveToken() {
        //  given
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addHeader("X-AUTH-TOKEN", "token resolve test");

        //  when
        String token = jwtTokenProvider.resolveToken(mockHttpServletRequest);

        //  then
        assertThat(token).isEqualTo("token resolve test");
    }

    @DisplayName("HttpHeader 에서 토큰 추출 실패(토큰 없음)")
    @Test
    void resolveTokenFailNotHasToken() {
        //  given
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();

        //  when
        String token = jwtTokenProvider.resolveToken(mockHttpServletRequest);

        //  then
        assertNull(token);
    }

    @DisplayName("JWT 토큰 생성 (ROLE_USER), 검증")
    @Test
    void createTokenGetUserPkAndValidateToken() {
        //  given
        String userPk = "monty@plgrim.com";
        List<String> roles = List.of("ROLE_USER");

        //  when
        String token = jwtTokenProvider.createToken(userPk, roles);

        //  then
        String name = jwtTokenProvider.getUserPk(token);
        assertThat(name).isEqualTo(userPk);                         //  토큰의 회원정보 검증
        assertTrue(() -> jwtTokenProvider.validateToken(token));    //  토큰의 유효성 검증
    }

    @DisplayName("JWT 토큰 인증 정보 조회")
    @Test
    void getAuthenticationFromToken() {
        //  given
        String userPk = "monty@plgrim.com";
        List<String> roles = List.of("ROLE_USER");
        UserJoinCommand userJoinCommand = UserJoinCommand.builder()
                .email("monty@plgrim.com")
                .password("testtest")
                .phoneNumber("01040684490")
                .userBasic(UserBasic.builder()
                        .address("동대문구")
                        .gender(Gender.MALE)
                        .birth(LocalDate.of(1994, 3, 30))
                        .snsType(Sns.LOCAL)
                        .build())
                .build();
        userJoinService.join(userJoinCommand);

        //  when
        String token = jwtTokenProvider.createToken(userPk, roles);
        Authentication authentication = jwtTokenProvider.getAuthentication(token);  // 토큰으로 유저 정보 조회
        User principal = (User) authentication.getPrincipal();   // 회원 정보 뽑아냄.

        //  then
        assertThat(principal).usingRecursiveComparison()
                .ignoringFields("usrNo", "password", "roles")
                .isEqualTo(userJoinCommand);
    }

    @DisplayName("JWT 토큰 인증 정보 조회 실패(없는 회원)")
    @Test
    void getAuthenticationFailUserNotFound() {
        //  given
        String userPk = "monty@plgrim.com";
        List<String> roles = List.of("ROLE_USER");

        //  when
        String token = jwtTokenProvider.createToken(userPk, roles);

        //  then
        String message = assertThrows(UsernameNotFoundException.class, () -> jwtTokenProvider.getAuthentication(token)).getMessage();
        assertThat(message).isEqualTo(ErrorCode.USER_NOT_FOUND.getDetail());
    }
}