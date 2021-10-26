package plgrim.sample.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import plgrim.sample.member.application.UserLoginService;
import plgrim.sample.member.controller.dto.user.UserLoginDTO;

import javax.validation.Valid;

import static plgrim.sample.common.KakaoValue.*;
import static plgrim.sample.common.UrlValue.*;

@Setter
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserLoginService userLoginService;

    /**
     * 유저 로그인
     */
    @PostMapping(ROOT_LOGIN_PATH)
    @ResponseBody
    public String login(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        return userLoginService.localLogin(userLoginDTO);
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
            add("client_id", KAPI_REST_API);
            add("redirect_uri", KAPI_API_REDIRECT_LOGIN_URL);
            add("response_type", "code");
        }};

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(KAKAO_LOGIN_PAGE_URL).queryParams(query);
        return "redirect:" + builder.toUriString();
    }

//    /**
//     * 카카오 로그아웃
//     * */
//    @GetMapping("kakao/logout")
//    public @ResponseBody
//    String logoutKakao(HttpServletRequest request) {
//        userLoginService.kakaoLogout(request);
//        return "success!";
//    }
}