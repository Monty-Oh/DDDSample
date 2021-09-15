package plgrim.sample.member.infrastructure.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import plgrim.sample.common.enums.Gender;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.member.domain.model.aggregate.User;
import plgrim.sample.member.domain.model.vo.UserBasic;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserRepository 테스트")
@DataJpaTest
class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    User user;
    User user2;
    User user3;

    @BeforeEach
    void setup() {
        user = User.builder()
                .id("monty@plgrim.com")
                .password("123456")
                .phoneNumber("01040684490")
                .userBasic(UserBasic.builder()
                        .address("동대문구")
                        .gender(Gender.MALE)
                        .birth(LocalDate.of(1994, 3, 30))
                        .snsType(Sns.LOCAL)
                        .build())
                .build();

        user2 = User.builder()
                .id("lizzy@plgrim.com")
                .password("123456")
                .phoneNumber("000")
                .userBasic(UserBasic.builder()
                        .address("동대문구")
                        .gender(Gender.MALE)
                        .birth(LocalDate.of(1994, 3, 30))
                        .snsType(Sns.LOCAL)
                        .build())
                .build();

        user3 = User.builder()
                .id("mandy@plgrim.com")
                .password("123456")
                .phoneNumber("0000")
                .userBasic(UserBasic.builder()
                        .address("동대문구")
                        .gender(Gender.MALE)
                        .birth(LocalDate.of(1994, 3, 30))
                        .snsType(Sns.LOCAL)
                        .build())
                .build();
    }

    @DisplayName("회원 정보 조회(ID)")
    @Test
    void findById() {
        userRepository.save(user);
        System.out.println(user);
        // 저장 후 조회해본다.
        Optional<User> result = userRepository.findById(user.getId());
        // 저장을 했는데 결과가 없으면 에러
        assertFalse(result.isEmpty());
        // 저장한 user의 ID와 불러온 유저의 정보가 같아야 한다. 내부 값 검증.
        assertThat(result.get())
                .usingRecursiveComparison()
                .isEqualTo(user);
    }

    @DisplayName("회원 정보 조회(전화번호)")
    @Test
    void findByPhoneNumber() {
        userRepository.save(user);
        // 저장 후 전화번호로 조회한다.
        Optional<User> result = userRepository.findByPhoneNumber(user.getPhoneNumber());
        // 저장을 했는데 결과가 없으면 에러
        assertFalse(result.isEmpty());
        // 저장한 user의 ID와 불러온 유저의 정보가 같아야 한다. 내부 값 검증.
        assertThat(result.get())
                .usingRecursiveComparison()
                .isEqualTo(user);
    }

    @DisplayName("회원 목록 조회")
    @Test
    void findAllUser() {
        // 테스트용 user 정보 저장
        userRepository.saveAll(Arrays.asList(user, user2, user3));

        // 회원 리스트 조회
        List<User> users = userRepository.findAll();
        // 회원이 제대로 저장되었고, 제대로 조회가 되었는지?
        assertThat(users.size()).isSameAs(3);
    }

    @DisplayName("회원 정보 수정")
    @Test
    void modifyUser() {
        userRepository.save(user);                                          // 테스트용 user 정보 저장
        UserBasic newUserBasic = UserBasic.builder()
                .address("testAddress")
                .gender(Gender.FEMALE)
                .birth(LocalDate.of(2021, 9, 3))
                .snsType(Sns.GOOGLE)
                .build();

        user.changePassword("testPassword");
        user.changePhoneNumber("00000000000");
        user.changeUserBasic(newUserBasic);

        assertThat(user.getPassword()).isEqualTo("testPassword");
        assertThat(user.getPhoneNumber()).isEqualTo("00000000000");
        assertThat(user.getUserBasic()).isSameAs(newUserBasic);
    }

    @DisplayName("회원 삭제")
    @Test
    void deleteById() {
        // 테스트용 user 정보 저장
        userRepository.save(user);
        // 삭제 시도
        userRepository.deleteById(user.getId());
        // null 이어야 함.
        Optional<User> result = userRepository.findById(user.getId());
        // 비어있으면 (해당 회원 정보가 없으면) 성공
        Assertions.assertTrue(result.isEmpty());
    }
}