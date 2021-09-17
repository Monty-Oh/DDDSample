package plgrim.sample.member.domain.service;

import plgrim.sample.member.domain.model.aggregates.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    /**
     * 아이디로 회원정보 조회
     */
    Optional<User> findById(String id);

    /**
     * 화번호로 회원정보 조회
     */
    Optional<User> findByPhoneNumber(String phoneNumber);

    /**
     * 회원 삭제 (ID)
     */
    void deleteById(String id);

    /**
     * 회원 조회 - 모두
     * */
    List<User> findAll();

    /**
     * 회원 저장
     * */
    User save(User user);

    /**
     * 회원 목록 저장
     * */
    <T extends User> List<T> saveAll(Iterable<T> entities);
}
