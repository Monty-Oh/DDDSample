package plgrim.sample.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import plgrim.sample.common.SHA256;
import plgrim.sample.common.enums.ErrorCode;
import plgrim.sample.common.exceptions.UserException;
import plgrim.sample.member.domain.model.commands.UserJoinCommand;
import plgrim.sample.member.domain.model.aggregates.User;
import plgrim.sample.member.domain.service.UserDomainService;
import plgrim.sample.member.domain.service.UserRepository;

@Service
@RequiredArgsConstructor
public class UserJoinService {
    private final UserRepository userRepository;        // 리포지토리
    private final UserDomainService userDomainService;  // 도메인 서비스
    private final SHA256 sha256;                        // 암호화 객체

    /**
     * 회원 가입
     */
    public String join(UserJoinCommand userJoinCommand) {
        if (userDomainService.checkDuplicateId(userJoinCommand.getId()))                      // 이미 있으면? 에러
            throw new UserException(ErrorCode.DUPLICATE_ID);

        if (userDomainService.checkDuplicatePhoneNumber(userJoinCommand.getPhoneNumber()))    // 만약 중복되는 phoneNumber가 있으면? 에러
            throw new UserException(ErrorCode.DUPLICATE_PHONE_NUMBER);

        User user = User.builder()
                .id(userJoinCommand.getId())
                .password(sha256.encrypt(userJoinCommand.getPassword()))
                .phoneNumber(userJoinCommand.getPhoneNumber())
                .userBasic(userJoinCommand.getUserBasic())
                .build();                               // 엔티티 객체로 변환

        userRepository.save(user);                      // user 저장
        return user.getId();                            // userNo 리턴
    }

    
}
