package plgrim.sample.member.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import plgrim.sample.common.enums.ErrorCode;
import plgrim.sample.common.exceptions.UserException;
import plgrim.sample.common.SHA256;
import plgrim.sample.member.domain.model.aggregates.User;
import plgrim.sample.member.infrastructure.repository.UserJPARepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDomainService {
    private final UserJPARepository userRepository;
    private final SHA256 sha256;

    /**
     * 이미 사용하는 아이디인지 체크
     * */
    public Boolean checkDuplicateEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);      // 아이디로 user 객체 가져옴
        return user.isPresent();                                // 이미 있으면 true, 없으면 false
    }

    /**
     * 이미 사용하는 전화번호인지 체크
     * */
    public Boolean checkDuplicatePhoneNumber(String phoneNumber) {
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);    // 전화번호로 조회
        return user.isPresent();                                                // 이미 있는 전화번호면 true 없으면 false
    }

    /**
     * 해당 id의 비밀번호가 일치한지 체크
     * */
    public Boolean compareUserPassword(String email, String password) {

        Optional<User> user = userRepository.findByEmail(email);          // id로 조회
        if (user.isEmpty()) throw new UserException(ErrorCode.MEMBER_NOT_FOUND);      // user가 없으면 에러

        return user.get().getPassword().equals(sha256.encrypt(password));
    }
}
