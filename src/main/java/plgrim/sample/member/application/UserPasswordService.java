package plgrim.sample.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import plgrim.sample.common.enums.ErrorCode;
import plgrim.sample.common.exceptions.UserException;
import plgrim.sample.member.domain.service.UserDomainService;

@Service
@RequiredArgsConstructor
public class UserPasswordService {
    private final UserDomainService userDomainService;  // 유저 도메인 객체

    /**
     * 비밀번호 일치
     */
    public Boolean comparePassword(String email, String password) {
        if (!userDomainService.compareUserPassword(email, password))
            throw new UserException(ErrorCode.INCORRECT_PASSWORD);
        return true;
    }
}
