package plgrim.sample.member.domain.model.aggregates;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import plgrim.sample.common.enums.Gender;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.member.domain.model.valueobjects.UserBasic;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("User Entity 테스트")
class UserTest {

    private User user;

    @BeforeEach
    void setup() {
        user = User.builder()
                .email("monty@plgrim.com")
                .password("123456")
                .mobileNo("01040684490")
                .userBasic(UserBasic.builder()
                        .address("동대문구")
                        .gender(Gender.MALE)
                        .birth(LocalDate.of(1994, 3, 30))
                        .snsType(Sns.LOCAL)
                        .build())
                .build();
    }

    @DisplayName("User 패스워드 변경")
    @Test
    void changePassword() {
        user.changePassword("newPassword");
        assertThat(user.getPassword()).isEqualTo("newPassword");
    }

    @DisplayName("User 기본 정보 변경")
    @Test
    void changeUserBasic() {
        user.changeUserBasic(UserBasic.builder()
                .address("동대문구2")
                .gender(Gender.FEMALE)
                .birth(LocalDate.of(2021, 9, 10))
                .snsType(Sns.GOOGLE)
                .build());
    }
}