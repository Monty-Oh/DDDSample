package plgrim.sample.member.application;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import plgrim.sample.common.LocalTokenProvider;
import plgrim.sample.common.enums.ErrorCode;
import plgrim.sample.common.exceptions.UserException;
import plgrim.sample.member.controller.dto.user.UserLoginDTO;
import plgrim.sample.member.domain.model.aggregates.User;
import plgrim.sample.member.infrastructure.repository.UserJPARepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("UserLoginService 테스트")
@ExtendWith(MockitoExtension.class)
class UserLoginServiceTest {
    @Mock
    UserJPARepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    LocalTokenProvider localTokenProvider;

    @InjectMocks
    UserLoginService userLoginService;

    UserLoginDTO userLoginDTO;
    User user;

    @BeforeEach
    void setup() {
        user = User.builder()
                .email("monty@plgrim.com")
                .password("testPassword")
                .roles(List.of("USER"))
                .build();

        userLoginDTO = UserLoginDTO.builder()
                .email("monty@plgrim.com")
                .password("testPassword")
                .build();
    }

    @DisplayName("로컬 로그인")
    @Test
    void localLogin() {
        //  given
        given(userRepository.findByEmail(userLoginDTO.getEmail())).willReturn(Optional.ofNullable(user));
        given(passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())).willReturn(true);
        given(localTokenProvider.createToken(userLoginDTO.getEmail(), user.getRoles())).willReturn("test Token");

        //  when
        String token = userLoginService.localLogin(userLoginDTO);

        //  then
        assertThat(token).isEqualTo("test Token");
    }

    @DisplayName("로컬 로그인 실패 - 없는 회원")
    @Test
    void localLoginFailUserNotFound() {
        //  given
        given(userRepository.findByEmail(userLoginDTO.getEmail())).willReturn(Optional.empty());

        //  when
        ErrorCode error = assertThrows(UserException.class, () -> userLoginService.localLogin(userLoginDTO)).getErrorCode();

        //  then
        assertThat(error).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @DisplayName("로컬 로그인 실패 - 비밀번호 틀림")
    @Test
    void localLoginFailIncorrectPassword() {
        //  given
        given(userRepository.findByEmail(userLoginDTO.getEmail())).willReturn(Optional.ofNullable(user));
        given(passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())).willReturn(false);

        //  when
        ErrorCode error = assertThrows(UserException.class, () -> userLoginService.localLogin(userLoginDTO)).getErrorCode();

        //  then
        assertThat(error).isEqualTo(ErrorCode.INCORRECT_PASSWORD);
    }
}