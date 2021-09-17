package plgrim.sample.member.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import plgrim.sample.common.enums.Gender;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.member.controller.dto.user.UserComparePasswordDTO;
import plgrim.sample.member.domain.model.commands.UserJoinCommand;
import plgrim.sample.member.domain.model.valueobjects.UserBasic;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserPasswordService 테스트")
@Transactional
@SpringBootTest
class UserPasswordValidationServiceTest {
    @Autowired
    UserPasswordService userPasswordService;

    @Autowired
    UserJoinService userJoinService;

    // 테스트 데이터
    UserJoinCommand userJoinCommand = UserJoinCommand.builder()
            .id("monty@plgrim.com")
            .password("test")
            .phoneNumber("01040684490")
            .userBasic(UserBasic.builder()
                    .address("동대문구")
                    .gender(Gender.MALE)
                    .birth(LocalDate.of(1994, 3, 30))
                    .snsType(Sns.LOCAL)
                    .build())
            .build();

    @DisplayName("비밀번호 일치")
    @Test
    void comparePassword() {
        String userId = userJoinService.join(userJoinCommand);                                  // 가입 후 ID 반환받음
        UserComparePasswordDTO userComparePasswordDTO = UserComparePasswordDTO.builder()    // 비밀번호 일치하는 테스트 DTO
                .id(userId)
                .password("test")
                .build();
        assertTrue(userPasswordService.comparePassword(userComparePasswordDTO));            // 일치함
    }

    @DisplayName("비밀번호 불일치")
    @Test
    void 비밀번호_틀림() {
        String userId = userJoinService.join(userJoinCommand);                                  // 가입 후 ID 반환받음
        UserComparePasswordDTO userComparePasswordDTO = UserComparePasswordDTO.builder()    // 비밀번호 불일치하는 테스트 DTO
                .id(userId)
                .password("wrongPassword")
                .build();
        assertFalse(userPasswordService.comparePassword(userComparePasswordDTO));           // 불일치함
    }
}