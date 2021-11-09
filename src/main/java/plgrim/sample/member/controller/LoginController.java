package plgrim.sample.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import plgrim.sample.member.application.UserLoginService;
import plgrim.sample.member.controller.dto.user.UserIdLoginDTO;

import javax.validation.Valid;

import static plgrim.sample.common.KakaoValue.*;
import static plgrim.sample.common.UrlValue.*;

@Setter
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserLoginService userLoginService;

    @Value("${sns.kakao.kauth.login-page}")
    String kakaoLoginPage;

    @Value("${sns.kakao.kapi.rest-api}")
    String kakaoRestApi;

    @Value("${sns.kakao.kapi.redirect-url}")
    String kakaoRedirectUrl;

    /**
     * 유저 로그인
     */
    @PostMapping(ROOT_LOGIN_PATH)
    @ResponseBody
    public String login(@Valid @RequestBody UserIdLoginDTO userIdLoginDto) {
        return userLoginService.localLogin(userIdLoginDto);
    }

    /**
     * 카카오 로그인.
     */
    @GetMapping(ROOT_LOGIN_PATH + KAKAO)
    @ResponseBody
    public String loginKakaoAuth(@RequestParam("code") String code) {
        return userLoginService.kakaoLogin(code);
    }

    /**
     * 카카오 로그인 페이지
     */
    @GetMapping(ROOT_LOGIN_PATH + KAKAO_VIEW)
    public String getLoginKakaoView() {
        MultiValueMap<String, String> query = new LinkedMultiValueMap<>() {{
            add("client_id", kakaoRestApi);
            add("redirect_uri", kakaoRedirectUrl);
            add("response_type", "code");
        }};

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(kakaoLoginPage).queryParams(query);
        return "redirect:" + builder.toUriString();
    }
}