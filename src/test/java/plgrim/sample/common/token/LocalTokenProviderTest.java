package plgrim.sample.common.token;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.member.domain.model.aggregates.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

@DisplayName("LocalTokenProvider 테스트")
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LocalTokenProviderTest {
    @Mock
    UserDetailsService userDetailsService;

    @InjectMocks
    LocalTokenProvider localTokenProvider;

    @DisplayName("Local 토큰 생성")
    @Test
    @Order(0)
    void createToken() {
        //  given
        ReflectionTestUtils.setField(localTokenProvider, "secretKey", "testSecretKey");

        //  when
        localTokenProvider.createToken("monty@plgrim.com", List.of("USER_ROLE"));
    }

    @DisplayName("Local 토큰 생성 후 userPk 조회")
    @Test
    @Order(1)
    void getUserPk() {
        //  given
        ReflectionTestUtils.setField(localTokenProvider, "secretKey", "testSecretKey");
        String token = localTokenProvider.createToken("monty@plgrim.com", List.of("USER_ROLE"));

        //  when
        String userPk = localTokenProvider.getUserPk(token);

        //  then
        assertThat(userPk).isEqualTo("monty@plgrim.com");
    }

//    @DisplayName("헤더에서 SnsType, Token 추출")
//    @Test
//    void resolveTokenAndSnsType() {
//        //  given
//        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest() {{
//            addHeader("X-AUTH-TOKEN", "test_token");
//            addHeader("X-SNS-TYPE", Sns.LOCAL.getValue());
//        }};
//
//        //  when
//        String token = localTokenProvider.resolveToken(httpServletRequest);
//        String sns = localTokenProvider.resolveSnsType(httpServletRequest);
//
//        //  then
//        assertThat(token).isEqualTo("test_token");
//        assertThat(sns).isEqualTo(Sns.LOCAL.getValue());
//    }

    @DisplayName("token 유효성, 만료일자 확인")
    @Test
    @Order(1)
    void validateToken() {
        //  given
        ReflectionTestUtils.setField(localTokenProvider, "secretKey", "testSecretKey");
        String token = localTokenProvider.createToken("monty@plgrim.com", List.of("USER_ROLE"));

        //  when
        boolean result = localTokenProvider.validateToken(token);

        //  then
        assertTrue(result);
    }

    @DisplayName("token 유효성, 만료일자 확인 - 없는 토큰")
    @Test
    @Order(1)
    void failValidateToken() {
        //  given
        ReflectionTestUtils.setField(localTokenProvider, "secretKey", "testSecretKey");
        String token = "test_token";

        //  when
        boolean result = localTokenProvider.validateToken(token);

        //  then
        assertFalse(result);
    }

    @DisplayName("인증 객체 반환")
    @Test
    @Order(2)
    @WithUserDetails(value = "monty@plgrim.com")
    void getAuthentication() {
        //  given
        ReflectionTestUtils.setField(localTokenProvider, "secretKey", "testSecretKey");
        String token = localTokenProvider.createToken("monty@plgrim.com", List.of("USER_ROLE"));
        UserDetails userDetails = User.builder()
                .usrNo(1L)
                .email("monty@plgrim.com")
                .build();
        given(userDetailsService.loadUserByUsername("monty@plgrim.com")).willReturn(userDetails);

        //  when
        Authentication authentication = localTokenProvider.getAuthentication(token);

        //  then
        assertThat(((User) authentication.getPrincipal()).getEmail()).isEqualTo(userDetails.getUsername());
    }
}