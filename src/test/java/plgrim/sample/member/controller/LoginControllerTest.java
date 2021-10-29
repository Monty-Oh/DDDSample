package plgrim.sample.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import plgrim.sample.common.KakaoTokenProvider;
import plgrim.sample.common.LocalTokenProvider;
import plgrim.sample.member.application.UserLoginService;
import plgrim.sample.member.controller.dto.user.UserLoginDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static plgrim.sample.common.KakaoValue.*;
import static plgrim.sample.common.UrlValue.*;

@DisplayName("UserLoginController 테스트")
@WebMvcTest
@MockBeans({
        @MockBean(LocalTokenProvider.class),
        @MockBean(KakaoTokenProvider.class),
        @MockBean(UserDetailsService.class),
        @MockBean(UserController.class)
})
class LoginControllerTest {
    @MockBean
    UserLoginService userLoginService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    UserLoginDTO userLoginDTO;

    @BeforeEach
    void setup() {
        userLoginDTO = UserLoginDTO.builder()
                .email("monty@plgrim.com")
                .password("password for test")
                .build();
    }

    @DisplayName("Local 로그인")
    @Test
    void localLogin() throws Exception {
        //  given
        given(userLoginService.localLogin(any())).willReturn("token");
        String content = objectMapper.writeValueAsString(userLoginDTO);

        //  when
        String result = mockMvc.perform(post(ROOT_LOGIN_PATH)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        //  then
        assertThat(result).isEqualTo("token");
    }

    @DisplayName("Kakao 로그인 페이지")
    @Test
    void getKakaoLoginPage() throws Exception {
        //  given
        MultiValueMap<String, String> query = new LinkedMultiValueMap<>() {{
            add("client_id", KAPI_REST_API);
            add("redirect_uri", KAPI_API_REDIRECT_LOGIN_URL);
            add("response_type", "code");
        }};
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(KAKAO_LOGIN_PAGE_URL).queryParams(query);

        //  when
        String redirectedUrl = mockMvc.perform(get(ROOT_LOGIN_PATH + KAKAO_VIEW))
                .andExpect(status().is3xxRedirection())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getRedirectedUrl();

        //  then
        assertThat(redirectedUrl).isEqualTo(builder.toUriString());
    }

    @DisplayName("Kakao 인가코드 받은 후")
    @Test
    void afterGetKakaoAuthCode() throws Exception{
        //  given
        String code = "testCode";
        String token = "test_access_token";
        given(userLoginService.kakaoLogin(code)).willReturn(token);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            add("code", code);
        }};

        //  when
        mockMvc.perform(get(ROOT_LOGIN_PATH + KAKAO)
                .queryParams(params))
                .andExpect(content().string(token))
                .andDo(print())
                .andReturn()
                .getResponse();
    }
}