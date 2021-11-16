package plgrim.sample.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import plgrim.sample.common.enums.ErrorCode;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.common.exceptions.UserException;
import plgrim.sample.common.token.KakaoTokenProvider;
import plgrim.sample.common.token.LocalTokenProvider;
import plgrim.sample.common.token.TokenProviderFactory;
import plgrim.sample.member.controller.dto.user.UserIdLoginDTO;
import plgrim.sample.member.domain.model.aggregates.User;
import plgrim.sample.member.domain.model.commands.UserJoinCommand;
import plgrim.sample.member.domain.model.entities.SnsInfo;
import plgrim.sample.member.infrastructure.repository.UserRepository;
import plgrim.sample.member.infrastructure.rest.dto.KakaoTokenDTO;
import plgrim.sample.member.infrastructure.rest.dto.KakaoUserInfoDTO;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserLoginService {
    private final UserRepository userRepository;        // 리포지토리
    private final PasswordEncoder passwordEncoder;
    private final LocalTokenProvider localTokenProvider;
    private final KakaoTokenProvider kakaoTokenProvider;
    private final TokenProviderFactory tokenProviderFactory;
    private final UserJoinService userJoinService;

    /**
     * 로그인 / SNS 로그인 정도로만 나누어보자.
     */


    // 로컬 로그인
    public String localLogin(UserIdLoginDTO userIdLoginDTO) {

        User user = userRepository.findByUserIdAndSnsType(userIdLoginDTO.getId(), Sns.LOCAL)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(userIdLoginDTO.getPassword(), user.getPassword()))
            throw new UserException(ErrorCode.INCORRECT_PASSWORD);

        return localTokenProvider.createToken(user.getUserId(), user.getRoles());
    }

    /**
     * 카카오 로그인
     * code 를 이용해 토큰 생성 요청 후
     * 토큰을 이용해 해당 정보를 요청하고
     * DB에서 ID를 조회, 없으면 새로 등록시킨다.
     * 있다면 refresh_token 을 업데이트 한다.
     */
    public String kakaoLogin(String code) {
        KakaoTokenDTO kakaoTokenDTO = kakaoTokenProvider.createToken(code);
        KakaoUserInfoDTO kakaoUserInfoDTO = kakaoTokenProvider.getUserInfo(kakaoTokenDTO.getAccess_token());

        Optional<User> result = userRepository.findByUserIdAndSnsType(kakaoUserInfoDTO.getId().toString(), Sns.KAKAO);

        System.out.println("kakaoUserInfoDTO = " + kakaoUserInfoDTO);

        if (result.isEmpty()) {
            userJoinService.join(UserJoinCommand.builder()
                    .userId(kakaoUserInfoDTO.getId().toString())
                    .email(kakaoUserInfoDTO.getKakao_account().containsKey("email") ?
                            kakaoUserInfoDTO.getKakao_account().get("email").toString() :
                            null)
                    .nickName(kakaoUserInfoDTO.getProperties().get("nickname"))
                    .mobileNo(kakaoUserInfoDTO.getKakao_account().containsKey("phone_number") ?
                            kakaoUserInfoDTO.getKakao_account().get("phone_number").toString() :
                            null)
                    .snsType(Sns.KAKAO)
                    .snsInfo(SnsInfo.builder()
                            .refreshToken(kakaoTokenDTO.getRefresh_token())
                            .tokenType(kakaoTokenDTO.getToken_type())
                            .scope(kakaoTokenDTO.getScope())
                            .build())
                    .build());
        } else {
            result.get().getSnsInfo().changeRefreshToken(kakaoTokenDTO.getRefresh_token());
        }

        return kakaoTokenDTO.getAccess_token();
    }
}
