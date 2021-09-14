package plgrim.sample.member.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plgrim.sample.member.domain.model.entity.User;

import java.util.Optional;

//@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
    * 아이디로 회원정보 조회
    * */
    Optional<User> findById(String id);

    /**
     * 화번호로 회원정보 조회
     * */
    Optional<User> findByPhoneNumber(String phoneNumber);

    /**
     * 회원 삭제 (ID)
     * */
    void deleteById(String id);
}
