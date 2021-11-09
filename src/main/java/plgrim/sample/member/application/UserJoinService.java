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
import plgrim.sample.member.domain.model.entities.UserRole;
import plgrim.sample.member.domain.service.UserDomainService;
import plgrim.sample.member.infrastructure.repository.UserRepository;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserJoinService {
    private final UserRepository userRepository;        // 리포지토리
    private final UserDomainService userDomainService;  // 도메인 서비스
    private final PasswordEncoder passwordEncoder;

    /**
     * userId, email, mobileNo 중복체크
     * Sns.Local
     */
    public UserDTO join(UserJoinCommand userJoinCommand) {
        this.checkDuplicate(userJoinCommand);

        // 엔티티 객체로 변환
        User user = User.builder()
                .userId(userJoinCommand.getUserId())
                .email(userJoinCommand.getEmail())
                .password(userJoinCommand.getSnsType().equals(Sns.LOCAL)
                        ? passwordEncoder.encode(userJoinCommand.getPassword())
                        : null)
                .nickName(userJoinCommand.getNickName())
                .mobileNo(userJoinCommand.getMobileNo())
                .snsType(userJoinCommand.getSnsType())
                .snsInfo(userJoinCommand.getSnsInfo())
                .userBasic(userJoinCommand.getUserBasic())
                .roles(Collections.singletonList(UserRole.builder()
                        .authority("ROLE_USER")
                        .build()))
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

    private void checkDuplicate(UserJoinCommand userJoinCommand) {
        if (userDomainService.checkDuplicateUserId(userJoinCommand.getUserId(), userJoinCommand.getSnsType()))
            throw new UserException(ErrorCode.DUPLICATE_USER_ID);

        if (userDomainService.checkDuplicateEmail(userJoinCommand.getEmail(), userJoinCommand.getSnsType()))
            throw new UserException(ErrorCode.DUPLICATE_EMAIL);

        if (userDomainService.checkDuplicateMobileNo(userJoinCommand.getMobileNo(), userJoinCommand.getSnsType()))    // 만약 중복되는 phoneNumber가 있으면? 에러
            throw new UserException(ErrorCode.DUPLICATE_MOBILE_NO);
    }
}
