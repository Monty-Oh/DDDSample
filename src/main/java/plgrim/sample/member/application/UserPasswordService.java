package plgrim.sample.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import plgrim.sample.common.SHA256;
import plgrim.sample.member.controller.dto.user.UserComparePasswordDTO;
import plgrim.sample.member.domain.service.UserDomainService;
import plgrim.sample.member.domain.service.UserRepository;

@Service
@RequiredArgsConstructor
public class UserPasswordService {
    private final UserRepository userRepository;        // 리포지토리
    private final UserDomainService userDomainService;  // 유저 도메인 객체
    private final SHA256 sha256;                        // 암호화 객체

    /**
     * 비밀번호 일치
     */
    public Boolean comparePassword(UserComparePasswordDTO userComparePasswordDto) {
        return userDomainService.compareUserPassword(userComparePasswordDto.getId(), userComparePasswordDto.getPassword());
    }
}
