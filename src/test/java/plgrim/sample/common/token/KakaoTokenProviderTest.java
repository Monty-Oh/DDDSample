package plgrim.sample.common.token;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;
import plgrim.sample.common.enums.ErrorCode;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.common.exceptions.UserException;
import plgrim.sample.member.domain.model.aggregates.User;
import plgrim.sample.member.domain.service.SnsStrategy;
import plgrim.sample.member.infrastructure.rest.dto.KakaoTokenDTO;
import plgrim.sample.member.infrastructure.rest.dto.KakaoUserInfoDTO;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static plgrim.sample.common.KakaoValue.KAPI_CHECK_ACCESS_TOKEN_URL;
import static plgrim.sample.common.KakaoValue.KAPI_USER_INFO_URL;

@DisplayName("KakaoTokenProvider 테스트")
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class KakaoTokenProviderTest {
    @Mock
    UserDetailsService userDetailsService;
    @Mock
    SnsStrategy snsStrategy;

    @InjectMocks
    KakaoTokenProvider kakaoTokenProvider;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(kakaoTokenProvider, "snsStrategy", snsStrategy);
    }

    @DisplayName("Kakao 토큰 생성")
    @Test
    void createToken() {
        //  given
        given(snsStrategy.getToken(any(), any()))
                .willReturn(KakaoTokenDTO.builder()
                        .access_token("test_token")
                        .build());

        //  when
        KakaoTokenDTO token = kakaoTokenProvider.createToken("test_code");

        //  then
        assertThat(token.getAccess_token()).isEqualTo("test_token");
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
//        String token = kakaoTokenProvider.resolveToken(httpServletRequest);
//        String sns = kakaoTokenProvider.resolveSnsType(httpServletRequest);
//
//        //  then
//        assertThat(token).isEqualTo("test_token");
//        assertThat(sns).isEqualTo(Sns.LOCAL.getValue());
//    }

    @DisplayName("카카오 토큰 검증 - 유효함")
    @Test
    void validateKakaoToken() {
        //  given
        given(snsStrategy.validateToken(KAPI_CHECK_ACCESS_TOKEN_URL, "test_token")).willReturn("nothing");

        //  when
        boolean result = kakaoTokenProvider.validateToken("test_token");

        //  then
        assertTrue(result);
    }

    static Stream<Arguments> getUserExceptionCase() {
        return Stream.of(
                Arguments.arguments(ErrorCode.EXPIRED_TOKEN),
                Arguments.arguments(ErrorCode.API_SERVER)
        );
    }

    @DisplayName("카카오 토큰 검증 - 실패(토큰 만료, API 서버 오류)")
    @ParameterizedTest(name = "토큰 검증 실패 UserException(ErrorCode.{0})")
    @MethodSource("getUserExceptionCase")
    void failValidateKakaoToken(ErrorCode errorCode) {
        //  given
        given(snsStrategy.validateToken(KAPI_CHECK_ACCESS_TOKEN_URL, "test_token")).willThrow(new UserException(errorCode));

        //  when
        boolean result = kakaoTokenProvider.validateToken("test_token");

        //  then
        assertFalse(result);
    }

    @DisplayName("카카오 회원 조회")
    @Test
    @Order(0)
    void getUserPk() {
        //  given
        given(snsStrategy.getUserInfo(KAPI_USER_INFO_URL, "test_token")).willReturn(KakaoUserInfoDTO.builder().id(1L).build());

        //  when
        String userPk = kakaoTokenProvider.getUserPk("test_token");

        //  then
        assertThat(userPk).isEqualTo(Long.toString(1L));
    }

    @DisplayName("카카오 인증 객체 생성")
    @Test
    @Order(1)
    void getAuthentication() {
        //  given
        given(snsStrategy.getUserInfo(KAPI_USER_INFO_URL, "test_token")).willReturn(KakaoUserInfoDTO.builder().id(1L).build());
        given(userDetailsService.loadUserByUsername(Long.toString(1L))).willReturn(User.builder().email(Long.toString(1L)).build());

        //  when
        Authentication result = kakaoTokenProvider.getAuthentication("test_token");

        //  then
        assertThat(((User) result.getPrincipal()).getEmail()).isEqualTo(Long.toString(1L));
    }
}