package plgrim.sample.member.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import plgrim.sample.common.enums.ErrorCode;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.common.exceptions.UserException;
import plgrim.sample.member.domain.model.aggregates.User;
import plgrim.sample.member.infrastructure.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDomainService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 이미 사용하는 로그인 아이디인지 체크
     */
    public Boolean checkDuplicateUserId(String userId, Sns snsType) {
        Optional<User> user = userRepository.findByUserIdAndSnsType(userId, snsType);
        return user.isPresent();
    }

    /**
     * 이미 사용하는 이메일인지 체크
     */
    public Boolean checkDuplicateEmail(String email, Sns snsType) {
        Optional<User> user = userRepository.findByEmailAndSnsType(email, snsType);
        return user.isPresent();                                    // 이미 있으면 true, 없으면 false
    }

    /**
     * 이미 사용하는 아이디인지 체크
     * 자신의 계정이라면 미사용으로 간주.
     * 자신의 계정이 아니며 누군가 사용중이면 변경 불가
     */
    public Boolean checkDuplicateEmailExceptOwn(String email, Sns snsType, String userId) {
        Optional<User> user = userRepository.findByEmailAndSnsType(email, snsType);
        return user.isPresent() && !user.get().getUserId().equals(userId);
    }

    /**
     * 이미 사용하는 전화번호인지 체크
     */
    public Boolean checkDuplicateMobileNo(String mobileNo, Sns snsType) {
        Optional<User> user = userRepository.findByMobileNoAndSnsType(mobileNo, snsType);    // 전화번호로 조회
        return user.isPresent();                                                // 이미 있는 전화번호면 true 없으면 false
    }

    /**
     * 이미 사용하는 전화번호인지 체크
     * 자신의 번호라면 통과.
     * 자신의 번호가 아니며 누군가 사용중이면 변경 불가
     */
    public Boolean checkDuplicatePhoneNumberExceptOwn(String mobileNo, Sns snsType, String userId) {
        Optional<User> user = userRepository.findByMobileNoAndSnsType(mobileNo, snsType);
        return user.isPresent() && !user.get().getUserId().equals(userId);
    }

    /**
     * 해당 id의 비밀번호가 일치한지 체크
     */
    public Boolean compareUserPassword(String userId, String password) {
        Optional<User> user = userRepository.findByUserIdAndSnsType(userId, Sns.LOCAL);          // id로 조회
        if (user.isEmpty()) throw new UserException(ErrorCode.USER_NOT_FOUND);      // user가 없으면 에러

        return user.get().getPassword().equals(passwordEncoder.encode(password));
    }
}
