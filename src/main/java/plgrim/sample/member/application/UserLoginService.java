package plgrim.sample.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import plgrim.sample.common.enums.ErrorCode;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.common.exceptions.UserException;
import plgrim.sample.common.token.KakaoTokenProvider;
import plgrim.sample.common.token.LocalTokenProvider;
import plgrim.sample.member.controller.dto.user.UserDTO;
import plgrim.sample.member.controller.dto.user.UserIdLoginDTO;
import plgrim.sample.member.domain.model.aggregates.User;
import plgrim.sample.member.domain.model.commands.UserJoinCommand;
import plgrim.sample.member.domain.model.valueobjects.SnsInfo;
import plgrim.sample.member.domain.service.UserDomainService;
import plgrim.sample.member.infrastructure.repository.UserJPARepository;
import plgrim.sample.member.infrastructure.rest.dto.KakaoTokenDTO;
import plgrim.sample.member.infrastructure.rest.dto.KakaoUserInfoDTO;

@Service
@RequiredArgsConstructor
public class UserLoginService {
    private final UserJPARepository userRepository;        // 리포지토리
    private final UserDomainService userDomainService;
    private final PasswordEncoder passwordEncoder;
    private final LocalTokenProvider localTokenProvider;
    private final KakaoTokenProvider kakaoTokenProvider;
    private final UserJoinService userJoinService;
    private final UserFindService userFindService;

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
     * DB에서 조회, 없으면 새로 등록시킨다.
     * 있다면 refresh_token 을 업데이트 한다.
     */
    public String kakaoLogin(String code) {
        KakaoTokenDTO kakaoTokenDTO = kakaoTokenProvider.createToken(code);
        KakaoUserInfoDTO kakaoUserInfoDTO = kakaoTokenProvider.getUserInfo(kakaoTokenDTO.getAccess_token());

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

        UserDTO userByUserIdAndSnsType = userFindService.findUserByUserIdAndSnsType(kakaoUserInfoDTO.getId().toString(), Sns.KAKAO.getValue());
        System.out.println("userByUserIdAndSnsType = " + userByUserIdAndSnsType);
//        userJoinService.join(UserCommandMapper)
//        Optional<User> result = userRepository.findByEmail(kakaoUserEmail);

//        // 비어있다면?
//        if (result.isEmpty()) {
//            User user = User.builder()
//                    .email(kakaoUserEmail)
//                    .refreshToken(kakaoTokenDTO.getRefresh_token())
//                    .roles(Collections.singletonList("ROLE_USER"))
//                    .userBasic(UserBasic.builder()
//                            .snsType(Sns.KAKAO)
//                            .build())
//                    .build();
//
//            userRepository.save(user);
//        }
        //  이미 있다면? 덮어씌운다.
//        else result.get().changeRefreshToken(kakaoTokenDTO.getRefresh_token());

        return kakaoTokenDTO.getAccess_token();
    }
}
