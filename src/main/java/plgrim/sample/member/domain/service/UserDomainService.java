package plgrim.sample.member.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import plgrim.sample.common.enums.ErrorCode;
import plgrim.sample.common.exceptions.UserException;
import plgrim.sample.common.SHA256;
import plgrim.sample.member.domain.model.aggregates.User;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDomainService {
    private final UserRepository userRepository;
    private final SHA256 sha256;

    /**
     * 이미 사용하는 아이디인지 체크
     * */
    public Boolean checkDuplicateId(String id) {
        Optional<User> user = userRepository.findById(id);      // 아이디로 user 객체 가져옴
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
    public Boolean compareUserPassword(String id, String password) {

        Optional<User> user = userRepository.findById(id);          // id로 조회
        if (user.isEmpty()) throw new UserException(ErrorCode.MEMBER_NOT_FOUND);      // user가 없으면 에러

        return user.get().getPassword().equals(sha256.encrypt(password));
    }
}
