package plgrim.sample.member.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import plgrim.sample.common.enums.ErrorCode;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.common.exceptions.UserException;
import plgrim.sample.common.token.LocalTokenProvider;
import plgrim.sample.member.controller.dto.user.UserIdLoginDTO;
import plgrim.sample.member.domain.model.aggregates.User;
import plgrim.sample.member.domain.model.entities.UserRole;
import plgrim.sample.member.infrastructure.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@DisplayName("UserLoginService 테스트")
@ExtendWith(MockitoExtension.class)
class UserLoginServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    LocalTokenProvider localTokenProvider;

    @InjectMocks
    UserLoginService userLoginService;

    UserIdLoginDTO userIdLoginDTO;
    User user;

    @BeforeEach
    void setup() {
        user = User.builder()
                .userId("monty")
                .email("monty@plgrim.com")
                .password("testPassword")
                .roles(List.of(UserRole.builder().authority("USER_ROLE").build()))
                .build();

        userIdLoginDTO = UserIdLoginDTO.builder()
                .id("monty")
                .password("testPassword")
                .build();
    }

    @DisplayName("로컬 로그인")
    @Test
    void localLogin() {
        //  given
        given(userRepository.findByUserIdAndSnsType(userIdLoginDTO.getId(), Sns.LOCAL)).willReturn(Optional.ofNullable(user));
        given(passwordEncoder.matches(userIdLoginDTO.getPassword(), user.getPassword())).willReturn(true);
        given(localTokenProvider.createToken(userIdLoginDTO.getId(), user.getRoles())).willReturn("test Token");

        //  when
        String token = userLoginService.localLogin(userIdLoginDTO);

        //  then
        assertThat(token).isEqualTo("test Token");
    }

    @DisplayName("로컬 로그인 실패 - 없는 회원")
    @Test
    void localLoginFailUserNotFound() {
        //  given
        given(userRepository.findByUserIdAndSnsType(userIdLoginDTO.getId(), Sns.LOCAL)).willReturn(Optional.empty());

        //  when
        ErrorCode error = assertThrows(UserException.class, () -> userLoginService.localLogin(userIdLoginDTO)).getErrorCode();

        //  then
        assertThat(error).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @DisplayName("로컬 로그인 실패 - 비밀번호 틀림")
    @Test
    void localLoginFailIncorrectPassword() {
        //  given
        given(userRepository.findByUserIdAndSnsType(userIdLoginDTO.getId(), Sns.LOCAL)).willReturn(Optional.ofNullable(user));
        given(passwordEncoder.matches(userIdLoginDTO.getPassword(), user.getPassword())).willReturn(false);

        //  when
        ErrorCode error = assertThrows(UserException.class, () -> userLoginService.localLogin(userIdLoginDTO)).getErrorCode();

        //  then
        assertThat(error).isEqualTo(ErrorCode.INCORRECT_PASSWORD);
    }

    @DisplayName("카카오 로그인 - 처음 로그인 시")
    @Test
    void kakaoLogin() {

    }
}