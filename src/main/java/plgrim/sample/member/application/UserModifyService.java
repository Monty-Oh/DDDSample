package plgrim.sample.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import plgrim.sample.common.enums.ErrorCode;
import plgrim.sample.common.exceptions.UserException;
import plgrim.sample.member.controller.dto.user.UserDTO;
import plgrim.sample.member.domain.model.aggregates.User;
import plgrim.sample.member.domain.model.commands.UserModifyCommand;
import plgrim.sample.member.domain.service.UserDomainService;
import plgrim.sample.member.infrastructure.repository.UserJPARepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserModifyService {
    private final UserJPARepository userRepository;        // 리포지토리
    private final UserDomainService userDomainService;
    private final PasswordEncoder passwordEncoder;


    /**
     * 회원정보 수정
     */
    public UserDTO modify(UserModifyCommand userModifyCommand) {
        Optional<User> user = userRepository.findByUserId(userModifyCommand.getUserId());
        if (user.isEmpty()) throw new UserException(ErrorCode.USER_NOT_FOUND);

        if (userDomainService.checkDuplicateEmailExceptOwn(userModifyCommand.getEmail(), userModifyCommand.getUsrNo()))
            throw new UserException(ErrorCode.DUPLICATE_ID);

        if (userDomainService.checkDuplicatePhoneNumberExceptOwn(userModifyCommand.getMobileNo(), userModifyCommand.getUsrNo()))
            throw new UserException(ErrorCode.DUPLICATE_PHONE_NUMBER);

        User result = userRepository.save(User.builder()
                .usrNo(user.get().getUsrNo())
                .email(userModifyCommand.getEmail())
                .password(passwordEncoder.encode(userModifyCommand.getPassword()))
                .nickName(userModifyCommand.getNickName())
                .mobileNo(userModifyCommand.getMobileNo())
                .snsType(userModifyCommand.getSnsType())
                .snsInfo(userModifyCommand.getSnsInfo())
                .userBasic(userModifyCommand.getUserBasic())
                .build());

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

    /**
     * 회원 탈퇴
     */
    public void delete(Long usrNo) {
        Optional<User> user = userRepository.findById(usrNo);   // user 조회 후 없으면? 못찾는 에러
        if (user.isEmpty()) throw new UserException(ErrorCode.USER_NOT_FOUND);

        userRepository.deleteById(usrNo);
    }
}
