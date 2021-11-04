package plgrim.sample.member.domain.service;

import plgrim.sample.common.enums.Sns;
import plgrim.sample.member.domain.model.aggregates.User;

import java.util.Optional;

public interface UserRepository {
    /**
     * 전화번호로 회원정보 조회
     */
    Optional<User> findByMobileNo(String phoneNumber);

    /**
     * 이메일로 회원 조회
     */
    Optional<User> findByEmail(String Email);

    /**
     * 로그인 ID로 회원 조회
     * */
    Optional<User> findByUserId(String id);
    
    /**
     * 로그인 ID, snsType 으로 회원 조회
     * */
    Optional<User> findByUserIdAndSnsType(String userId, Sns snsType);

    /**
     * 이메일, snsType 으로 회원 조회
     * */
    Optional<User> findByEmailAndSnsType(String email, Sns snsType);
    
    /**
     * 전화번호, snsType 으로 회원 조회
     * */
    Optional<User> findByMobileNoAndSnsType(String mobileNo, Sns snsType);

    /**
     * 로그인 아이디로 회원 삭제
     */
    void deleteByUserIdAndSnsType(String email, Sns snsType);
}
