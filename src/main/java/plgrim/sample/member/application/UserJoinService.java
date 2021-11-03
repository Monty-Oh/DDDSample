package plgrim.sample.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import plgrim.sample.common.enums.ErrorCode;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.common.exceptions.UserException;
import plgrim.sample.member.controller.dto.user.UserDTO;
import plgrim.sample.member.domain.model.aggregates.User;
import plgrim.sample.member.domain.model.commands.UserJoinCommand;
import plgrim.sample.member.domain.service.UserDomainService;
import plgrim.sample.member.infrastructure.repository.UserJPARepository;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserJoinService {
    private final UserJPARepository userRepository;        // 리포지토리
    private final UserDomainService userDomainService;  // 도메인 서비스
    private final PasswordEncoder passwordEncoder;

    /**
     * local 일 때 email 은 유일 키
     * local 일 때 mobileNo은 유일 키
     */
    public UserDTO join(UserJoinCommand userJoinCommand) {
        if (userJoinCommand.getSnsType().equals(Sns.LOCAL)) {
            if (userDomainService.checkDuplicateEmail(userJoinCommand.getEmail()))
                throw new UserException(ErrorCode.DUPLICATE_ID);

            if (userDomainService.checkDuplicatePhoneNumber(userJoinCommand.getMobileNo()))    // 만약 중복되는 phoneNumber가 있으면? 에러
                throw new UserException(ErrorCode.DUPLICATE_PHONE_NUMBER);
        }

        // 엔티티 객체로 변환
        User user = User.builder()
                .userId(userJoinCommand.getSnsType().equals(Sns.LOCAL) ?
                        userJoinCommand.getUserId() :
                        userJoinCommand.getUserId() + "_" + userJoinCommand.getSnsType().getValue())
                .email(userJoinCommand.getEmail())
                .password(passwordEncoder.encode(userJoinCommand.getPassword()))
                .nickName(userJoinCommand.getNickName())
                .mobileNo(userJoinCommand.getMobileNo())
                .snsType(userJoinCommand.getSnsType())
                .snsInfo(userJoinCommand.getSnsInfo())
                .userBasic(userJoinCommand.getUserBasic())
                .roles(Collections.singletonList("ROLE_USER"))
                .build();

        // user 저장
        User result = userRepository.save(user);

        // usrNo 반환
        return UserDTO.builder()
                .usrNo(result.getUsrNo())
                .userId(result.getUserId())
                .email(result.getEmail())
                .mobileNo(result.getMobileNo())
                .snsType(result.getSnsType())
                .snsInfo(result.getSnsInfo())
                .userBasic(result.getUserBasic())
                .build();
    }
}
