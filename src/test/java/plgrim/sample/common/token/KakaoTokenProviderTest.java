package plgrim.sample.common.token;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetailsService;
import plgrim.sample.member.domain.service.SnsStrategy;
import plgrim.sample.member.domain.service.SnsStrategyFactory;
import plgrim.sample.member.infrastructure.rest.dto.KakaoTokenDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static plgrim.sample.common.KakaoValue.KAPI_GET_TOKEN_URL;

@DisplayName("KakaoTokenProvider 테스트")
@ExtendWith(MockitoExtension.class)
class KakaoTokenProviderTest {
    @Mock
    UserDetailsService userDetailsService;
    @Mock
    SnsStrategyFactory snsStrategyFactory;
    @Mock
    SnsStrategy snsStrategy;

    @InjectMocks
    KakaoTokenProvider kakaoTokenProvider;

//    @BeforeAll
//    void init() {
//        given(snsStrategyFactory.findSnsStrategy(Sns.KAKAO)).willReturn
//    }

    @DisplayName("Kakao 토큰 생성")
    @Test
    void createToken() {
        //  given
        given(snsStrategy.getToken(KAPI_GET_TOKEN_URL, "test_code"))
                .willReturn(KakaoTokenDTO.builder()
                        .access_token("test_token")
                        .build());

        //  when
        KakaoTokenDTO token = kakaoTokenProvider.createToken("test_code");

        //  then
        assertThat(token.getAccess_token()).isEqualTo("test_token");
    }


}