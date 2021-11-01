//package plgrim.sample.member.infrastructure.rest;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.web.client.RestTemplateBuilder;
//import org.springframework.http.*;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.util.UriComponentsBuilder;
//import plgrim.sample.common.enums.ErrorCode;
//import plgrim.sample.common.exceptions.UserException;
//import plgrim.sample.member.infrastructure.rest.dto.KakaoTokenDTO;
//import plgrim.sample.member.infrastructure.rest.dto.KakaoUserInfoDTO;
//import plgrim.sample.member.infrastructure.rest.dto.KakaoValidateTokenDTO;
//import reactor.core.publisher.Flux;
//
//import static plgrim.sample.common.KakaoValue.KAPI_API_REDIRECT_LOGIN_URL;
//import static plgrim.sample.common.KakaoValue.KAPI_REST_API;
//
//@Component
//@RequiredArgsConstructor
//public class KakaoRestApiService {
//
//    /**
//     * 카카오 토큰요청
//     * 엑세스 토큰 - 로그인 하는데 필요한 짧은 만료시간을 가진 토큰
//     * 리프레시 토큰 - 상대적으로 긴 시간을 가짐. 리프레시 토큰으로 엑세스 토큰을 재발급 받을 수 있음.
//     */
//    public KakaoTokenDTO getKakaoAccessTokensUsingAuthCode(String url, String code) {
//
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
//            add("grant_type", "authorization_code");
//            add("client_id", KAPI_REST_API);
//            add("redirect_uri", KAPI_API_REDIRECT_LOGIN_URL);
//            add("code", code);
//        }};
//
//        Flux<KakaoTokenDTO> response = WebClient.create(url)
//                .post()
//                .uri(uriBuilder -> uriBuilder.queryParams(params).build())
//                .headers(httpHeaders -> httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE))
//                .retrieve()
//                .bodyToFlux(KakaoTokenDTO.class);
//
//        return response.blockFirst();
//    }
//
//    public KakaoValidateTokenDTO validateToken(String url, String token) {
//        try {
//            Flux<KakaoValidateTokenDTO> response = WebClient.create(url)
//                    .get()
//                    .headers(httpHeaders -> {
//                        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
//                        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
//                    })
//                    .retrieve()
//                    .bodyToFlux(KakaoValidateTokenDTO.class);
//
//            return response.blockFirst();
//
//        } catch (HttpClientErrorException exception) {
//            // 토큰이 만료되었을 때
//            if (exception.getStatusCode().value() == 401)
//                throw new UserException(ErrorCode.EXPIRED_TOKEN);
//            else
//                throw new UserException(ErrorCode.API_SERVER);
//        }
//    }
//
//    public KakaoUserInfoDTO getKakaoUserInfo(String url, String token) {
//        try {
//            Flux<KakaoUserInfoDTO> response = WebClient.create(url)
//                    .get()
//                    .headers(httpHeaders -> {
//                        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
//                        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
//                    })
//                    .retrieve()
//                    .bodyToFlux(KakaoUserInfoDTO.class);
//
//            return response.blockFirst();
//        } catch (HttpClientErrorException exception) {
//            if (exception.getStatusCode().value() == 401)
//                throw new UserException(ErrorCode.TOKEN_NOT_EXIST);
//        }
//
//        return null;
//    }
//}
