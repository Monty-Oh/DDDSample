package plgrim.sample.member.domain.service;

import plgrim.sample.member.domain.model.aggregates.User;

import java.util.Optional;

public interface UserRepository {
    /**
     * 전화번호로 회원정보 조회
     */
    Optional<User> findByPhoneNumber(String phoneNumber);

    /**
     * 이메일로 회원 조회
     */
    Optional<User> findByEmail(String Email);

    /**
     * 로그인 ID로 회원 조회
     * */
    Optional<User> findByUserId(String id);

    /**
     * 이메일로 회원 삭제
     */
    void deleteByEmail(String email);
}
