package plgrim.sample.member.domain.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import plgrim.sample.member.domain.model.aggregates.User;

@DisplayName("UserDomainService 테스트")
@SpringBootTest
class UserDomainServiceTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserDomainService userDomainService;

    User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id("monty@plgrim.com")
                .phoneNumber("01040684490")
                .build();
    }

    @DisplayName("회원 중복 체크 - 아이디")
    @Test
    void checkDuplicateId() {
        userRepository.save(user);

        Assertions.assertTrue(userDomainService.checkDuplicateId("monty@plgrim.com"));      // 이미 존재하는 회원
        Assertions.assertFalse(userDomainService.checkDuplicateId("monty@plgrim.com2"));    // 없는 회원
    }

    @DisplayName("회원 중복 체크 - 전화번호")
    @Test
    void checkDuplicatePhoneNumber() {
        userRepository.save(user);

        Assertions.assertTrue(userDomainService.checkDuplicatePhoneNumber("01040684490"));  // 이미 존재하는 회원
        Assertions.assertFalse(userDomainService.checkDuplicatePhoneNumber("01040684491")); // 없는 회원
    }
}