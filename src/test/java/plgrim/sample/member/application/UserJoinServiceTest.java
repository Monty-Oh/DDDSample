package plgrim.sample.member.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import plgrim.sample.common.SHA256;
import plgrim.sample.common.enums.ErrorCode;
import plgrim.sample.common.enums.Gender;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.common.exceptions.UserException;
import plgrim.sample.member.controller.dto.user.UserDTO;
import plgrim.sample.member.domain.model.aggregates.User;
import plgrim.sample.member.domain.model.commands.UserJoinCommand;
import plgrim.sample.member.domain.model.valueobjects.UserBasic;
import plgrim.sample.member.domain.service.UserDomainService;
import plgrim.sample.member.infrastructure.repository.UserJPARepository;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("UserJoinService 테스트")
@ExtendWith(MockitoExtension.class)
class UserJoinServiceTest {
    @Mock
    UserJPARepository userRepository;

    @Mock
    UserDomainService userDomainService;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserJoinService userJoinService;

    // 테스트 데이터
    UserJoinCommand userJoinCommand;
    User user;

    @BeforeEach
    void setup() {
        userJoinCommand = UserJoinCommand.builder()
                .email("monty@plgrim.com")
                .password("test")
                .phoneNumber("01040684490")
                .userBasic(UserBasic.builder()
                        .address("동대문구")
                        .gender(Gender.MALE)
                        .birth(LocalDate.of(1994, 3, 30))
                        .snsType(Sns.LOCAL)
                        .build())
                .build();

        user = User.builder()
                .usrNo(1L)
                .email("monty@plgrim.com")
                .password("encrypted password")
                .phoneNumber("01040684490")
                .userBasic(UserBasic.builder()
                        .address("동대문구")
                        .gender(Gender.MALE)
                        .birth(LocalDate.of(1994, 3, 30))
                        .snsType(Sns.LOCAL)
                        .build())
                .build();
    }

    @DisplayName("회원가입 성공")
    @Test
    void joinUser() {
        //  given
        given(passwordEncoder.encode(userJoinCommand.getPassword())).willReturn("encrypted password");
        given(userDomainService.checkDuplicateEmail(userJoinCommand.getEmail())).willReturn(false);
        given(userDomainService.checkDuplicatePhoneNumber(userJoinCommand.getPhoneNumber())).willReturn(false);
        given(userRepository.save(any())).willReturn(user);

        //  when
        UserDTO result = userJoinService.join(userJoinCommand);

        //  then
        assertThat(result.getEmail()).isEqualTo(userJoinCommand.getEmail());
    }

    @DisplayName("회원가입 실패 - id 중복가입")
    @Test
    void joinUserFailDuplicatedId() {
        //  given
        given(userDomainService.checkDuplicateEmail(userJoinCommand.getEmail())).willReturn(true);

        //  when
        ErrorCode error = assertThrows(UserException.class, () -> userJoinService.join(userJoinCommand)).getErrorCode();

        //  then
        assertThat(error).isEqualTo(ErrorCode.DUPLICATE_ID);
    }

    @DisplayName("회원가입 실패 - PhoneNum 중복가입")
    @Test
    void joinUserFailDuplicatedPhoneNum() {
        //  given
        given(userDomainService.checkDuplicateEmail(userJoinCommand.getEmail())).willReturn(false);
        given(userDomainService.checkDuplicatePhoneNumber(userJoinCommand.getPhoneNumber())).willReturn(true);

        //  when
        ErrorCode error = assertThrows(UserException.class, () -> userJoinService.join(userJoinCommand)).getErrorCode();

        //  then
        assertThat(error).isEqualTo(ErrorCode.DUPLICATE_PHONE_NUMBER);
    }
}