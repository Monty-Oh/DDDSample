package plgrim.sample.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import plgrim.sample.common.KakaoTokenProvider;
import plgrim.sample.common.LocalTokenProvider;
import plgrim.sample.common.enums.ErrorCode;
import plgrim.sample.common.exceptions.UserException;
import plgrim.sample.member.controller.dto.user.UserLoginDTO;
import plgrim.sample.member.domain.model.aggregates.User;
import plgrim.sample.member.infrastructure.repository.UserJPARepository;
import plgrim.sample.member.infrastructure.rest.KakaoRestApiService;
import plgrim.sample.member.infrastructure.rest.dto.KakaoTokenDTO;

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
     * */
    public String kakaoLogin(String code) {

        KakaoTokenDTO kakaoTokenDTO = kakaoTokenProvider.createToken(code);
//        String kakaoUserInfo = kakaoRestApiService.getKakaoUserInfo(KAPI_USER_INFO_URL, kakaoTokenDTO.getAccess_token());

//        System.out.println("kakaoUserInfo = " + kakaoUserInfo);

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
