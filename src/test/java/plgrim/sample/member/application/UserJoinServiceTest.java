package plgrim.sample.member.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import plgrim.sample.common.enums.Gender;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.common.exceptions.UserException;
import plgrim.sample.member.domain.model.commands.UserJoinCommand;
import plgrim.sample.member.domain.model.vo.UserBasic;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("UserJoinService 테스트")
@Transactional
@SpringBootTest
class UserJoinServiceTest {
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

    @DisplayName("회원가입 성공")
    @Test
    void joinUser() {
        String userId = userJoinService.join(userJoinCommand);  // 가입 후 ID 반환받음
        assertThat(userId).isEqualTo("monty@plgrim.com");       // 반환 값으로 받은 email 비교
    }

    @DisplayName("회원가입 실패 - id 중복가입")
    @Test
    void 중복가입_ID() {
        userJoinService.join(userJoinCommand);                  // 가입을 먼저 시켜놓는다.
        assertThrows(UserException.class,
                () -> userJoinService.join(userJoinCommand));   // 이미 가입이 되어있으므로 에러가 나야한다.
    }

    @DisplayName("회원가입 실패 - phone 중복가입")
    @Test
    void 중복가입_전화번호() {
        userJoinService.join(userJoinCommand);                  // 가입을 먼저 시켜놓는다.

        UserJoinCommand userJoinCommand = UserJoinCommand.builder()     // 전화번호만 같은 테스트 데이터
                .id("monty@plgrim.com2")
                .password("test")
                .phoneNumber("01040684490")
                .userBasic(UserBasic.builder()
                        .address("동대문구")
                        .gender(Gender.MALE)
                        .birth(LocalDate.of(1994, 3, 30))
                        .snsType(Sns.LOCAL)
                        .build())
                .build();

        assertThrows(UserException.class,
                () -> userJoinService.join(userJoinCommand));   // 이미 가입이 되어있으므로 에러가 나야한다.
    }
}