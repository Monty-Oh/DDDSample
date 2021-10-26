package plgrim.sample.member.infrastructure.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import plgrim.sample.common.enums.ErrorCode;
import plgrim.sample.common.exceptions.UserException;
import plgrim.sample.member.infrastructure.rest.dto.KakaoTokenDTO;
import plgrim.sample.member.infrastructure.rest.dto.KakaoUserInfoDTO;
import plgrim.sample.member.infrastructure.rest.dto.KakaoValidateTokenDTO;

import static plgrim.sample.common.KakaoValue.KAPI_API_REDIRECT_LOGIN_URL;
import static plgrim.sample.common.KakaoValue.KAPI_REST_API;

@Service
@RequiredArgsConstructor
public class KakaoRestApiService {

    //  호출 이후 응답 때까지 기다리는 동기방식
    //  비동기 방식은 WebClient (AsyncResTemplate 는 Deprecated 됨)
    private final RestTemplateBuilder restTemplateBuilder;

    /**
     * 카카오 토큰요청
     * 엑세스 토큰 - 로그인 하는데 필요한 짧은 만료시간을 가진 토큰
     * 리프레시 토큰 - 상대적으로 긴 시간을 가짐. 리프레시 토큰으로 엑세스 토큰을 재발급 받을 수 있음.
     */
    public KakaoTokenDTO getKakaoLoginTokenUsingAuthCode(String url, String code) {

        //  토큰 요청 파라미터
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            add("grant_type", "authorization_code");
            add("client_id", KAPI_REST_API);
            add("redirect_uri", KAPI_API_REDIRECT_LOGIN_URL);
            add("code", code);
        }};

        //  HttpHeader 생성
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        //  HttpHeader 와 body 를 하나의 오브젝트로
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params, httpHeaders);

        //  http 요청 객체
        RestTemplate rt = restTemplateBuilder.build();

        //  카카오 토큰 발급 요청
        ResponseEntity<KakaoTokenDTO> response = rt.exchange(
                url,
                HttpMethod.POST,
                kakaoTokenRequest,
                KakaoTokenDTO.class
        );

        return response.getBody();
    }

    public void validateToken(String url, String token) {
        HttpEntity<MultiValueMap<String, String>> httpEntity =
                this.buildRestTemplateIncludeToken(token);

        //  http 요청 객체
        RestTemplate rt = restTemplateBuilder.build();

        try {
            //  카카오 토큰 검사
            rt.exchange(
                    url,
                    HttpMethod.GET,
                    httpEntity,
                    KakaoValidateTokenDTO.class
            );

        } catch (HttpClientErrorException exception) {
            // 토큰이 만료되었을 때
            if (exception.getStatusCode().value() == 401)
                throw new UserException(ErrorCode.EXPIRED_TOKEN);
            else
                throw new UserException(ErrorCode.API_SERVER);
        }
    }

    public String getKakaoUserInfo(String url, String token) {
        HttpEntity<MultiValueMap<String, String>> httpEntity =
                this.buildRestTemplateIncludeToken(token);

        //  http 요청 객체
        RestTemplate rt = restTemplateBuilder.build();

        try {
            ResponseEntity<KakaoUserInfoDTO> result;
            //  카카오 계정 정보 조회
            result = rt.exchange(
                    url,
                    HttpMethod.GET,
                    httpEntity,
                    KakaoUserInfoDTO.class
            );

            if (result.getBody() == null) throw new UserException(ErrorCode.USER_NOT_FOUND);

            System.out.println("result.getBody().getProperties() = " + result.getBody().getProperties());

            return result.getBody().getProperties().get("nickname");
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode().value() == 401)
                throw new UserException(ErrorCode.TOKEN_NOT_EXIST);
        }

        return null;
    }
    // 에러 시 [{"msg":"this access token does not exist","code":-401}]

//    public String logoutKakao(String token) {
////        //  KAKAO_REDIRECT_LOGOUT_URL
////        //  토큰 요청 파라미터
////        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
////            add("client_id", KAKAO_REST_API);
////            add("logout_redirect_uri", KAKAO_REDIRECT_LOGOUT_URL);
////        }};
////
////        //  HttpHeader 생성
////        HttpHeaders httpHeaders = new HttpHeaders();
////        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
////
////        //  HttpHeader 와 body 를 하나의 오브젝트로
////        HttpEntity<MultiValueMap<String, String>> httpEntity =
////                new HttpEntity<>(params, httpHeaders);
//
//        HttpEntity<MultiValueMap<String, String>> httpEntity =
//                this.buildRestTemplateIncludeToken(token);
//
//        //  http 요청 객체
//        RestTemplate rt = restTemplateBuilder.build();
//
//        //  카카오 로그아웃
//        ResponseEntity<String> exchange = rt.exchange(
//                KAKAO_LOGOUT_URL,
//                HttpMethod.POST,
//                httpEntity,
//                String.class
//        );
//
//        System.out.println("exchange = " + exchange);
//
//        return null;
//    }

    //  HttpEntity 반환 메소드
    private HttpEntity<MultiValueMap<String, String>> buildRestTemplateIncludeToken(String token) {
        //  HttpHeader 생성
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        //  HttpHeader 와 body 를 하나의 오브젝트로
        return new HttpEntity<>(httpHeaders);
    }
}
