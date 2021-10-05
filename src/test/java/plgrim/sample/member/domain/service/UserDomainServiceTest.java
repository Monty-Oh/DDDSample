package plgrim.sample.member.domain.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import plgrim.sample.member.domain.model.aggregates.User;
import plgrim.sample.member.infrastructure.repository.UserJPARepository;

@DisplayName("UserDomainService 테스트")
@Transactional
@SpringBootTest
class UserDomainServiceTest {
    @Autowired
    UserJPARepository userRepository;
    @Autowired
    UserDomainService userDomainService;

    User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("monty@plgrim.com")
                .phoneNumber("01040684490")
                .build();
    }

    @DisplayName("회원 중복 체크 - 아이디")
    @Test
    void checkDuplicateId() {
        userRepository.save(user);

        Assertions.assertTrue(userDomainService.checkDuplicateEmail("monty@plgrim.com"));      // 이미 존재하는 회원
        Assertions.assertFalse(userDomainService.checkDuplicateEmail("monty@plgrim.com2"));    // 없는 회원
    }

    @DisplayName("회원 중복 체크 - 전화번호")
    @Test
    void checkDuplicatePhoneNumber() {
        userRepository.save(user);

        Assertions.assertTrue(userDomainService.checkDuplicatePhoneNumber("01040684490"));  // 이미 존재하는 회원
        Assertions.assertFalse(userDomainService.checkDuplicatePhoneNumber("01040684491")); // 없는 회원
    }
}