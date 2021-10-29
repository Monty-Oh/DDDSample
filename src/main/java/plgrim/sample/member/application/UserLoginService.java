package plgrim.sample.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import plgrim.sample.common.KakaoTokenProvider;
import plgrim.sample.common.LocalTokenProvider;
import plgrim.sample.common.enums.ErrorCode;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.common.exceptions.UserException;
import plgrim.sample.member.controller.dto.user.UserLoginDTO;
import plgrim.sample.member.domain.model.aggregates.User;
import plgrim.sample.member.domain.model.valueobjects.UserBasic;
import plgrim.sample.member.infrastructure.repository.UserJPARepository;
import plgrim.sample.member.infrastructure.rest.KakaoRestApiService;
import plgrim.sample.member.infrastructure.rest.dto.KakaoTokenDTO;
import plgrim.sample.member.infrastructure.rest.dto.KakaoUserInfoDTO;

import java.util.Collections;
import java.util.Optional;

import static plgrim.sample.common.KakaoValue.KAPI_USER_INFO_URL;

@Service
@RequiredArgsConstructor
public class UserLoginService {
    private final UserJPARepository userRepository;        // 리포지토리
    private final PasswordEncoder passwordEncoder;
    private final LocalTokenProvider localTokenProvider;
    private final KakaoTokenProvider kakaoTokenProvider;
    private final KakaoRestApiService kakaoRestApiService;

    // 로컬 로그인
    public String localLogin(UserLoginDTO userLoginDTO) {
        User user = userRepository.findByEmail(userLoginDTO.getEmail())
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword()))
            throw new UserException(ErrorCode.INCORRECT_PASSWORD);

        return localTokenProvider.createToken(user.getEmail(), user.getRoles());
    }

    /**
     * 카카오 로그인
     */
    public String kakaoLogin(String code) {

        //  카카오로부터 토큰 생성 요청
        KakaoTokenDTO kakaoTokenDTO = kakaoTokenProvider.createToken(code);

        //  카카오로부터 토큰을 이용해 해당 정보 요청
        KakaoUserInfoDTO kakaoUserInfo = kakaoRestApiService.getKakaoUserInfo(KAPI_USER_INFO_URL, kakaoTokenDTO.getAccess_token());

        Optional<User> result = userRepository.findByEmail(kakaoUserInfo.getId().toString());

        // 비어있다면?
        if (result.isEmpty()) {
            User user = User.builder()
                    .email(kakaoUserInfo.getId().toString())
                    .refreshToken(kakaoTokenDTO.getRefresh_token())
                    .roles(Collections.singletonList("ROLE_USER"))
                    .userBasic(UserBasic.builder()
                            .snsType(Sns.KAKAO)
                            .build())
                    .build();

            userRepository.save(user);
            System.out.println("user = " + user);
        }
        //  이미 있다면? 덮어씌운다.
        else result.get().changeRefreshToken(kakaoTokenDTO.getRefresh_token());


        return kakaoTokenDTO.getAccess_token();
    }

//    /**
//     * 카카오 로그아웃
//     * */
//    public String kakaoLogout(HttpServletRequest request) {
//        String token = kakaoTokenProvider.resolveToken(request);
//        kakaoService.logoutKakao(token);
//
//        return null;
//    }
}
