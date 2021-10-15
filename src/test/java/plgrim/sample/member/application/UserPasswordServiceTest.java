package plgrim.sample.member.application;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import plgrim.sample.common.enums.ErrorCode;
import plgrim.sample.common.exceptions.UserException;
import plgrim.sample.member.domain.service.UserDomainService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

@DisplayName("UserPasswordService 테스트")
@ExtendWith(MockitoExtension.class)
class UserPasswordServiceTest {
    @Mock
    UserDomainService userDomainService;

    @InjectMocks
    UserPasswordService userPasswordService;

    String email = "monty@plgrim.com";
    String password = "password";

    @DisplayName("비밀번호 검사 일치")
    @Test
    void comparePasswordCorrect() {
        //  given
        given(userDomainService.compareUserPassword(email, password)).willReturn(true);

        //  when    //  then
        assertDoesNotThrow(() -> userPasswordService.comparePassword(email, password));
        assertTrue(() -> userPasswordService.comparePassword(email, password));
    }

    @DisplayName("비밀번호 검사 불일치")
    @Test
    void comparePasswordInCorrect() {
        //  given
        given(userDomainService.compareUserPassword(email, password)).willReturn(false);

        //  when
        ErrorCode error = Assertions.assertThrows(UserException.class,
                () -> userPasswordService.comparePassword(email, password)).getErrorCode();

        //  then
        assertThat(error).isEqualTo(ErrorCode.INCORRECT_PASSWORD);
    }
}