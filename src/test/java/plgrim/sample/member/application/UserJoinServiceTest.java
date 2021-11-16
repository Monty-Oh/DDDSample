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
import plgrim.sample.common.enums.Gender;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.common.exceptions.UserException;
import plgrim.sample.member.controller.dto.user.UserDTO;
import plgrim.sample.member.domain.model.aggregates.User;
import plgrim.sample.member.domain.model.commands.UserJoinCommand;
import plgrim.sample.member.domain.model.entities.SnsInfo;
import plgrim.sample.member.domain.model.valueobjects.UserBasic;
import plgrim.sample.member.domain.service.UserDomainService;
import plgrim.sample.member.infrastructure.repository.UserRepository;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("UserJoinService 테스트")
@ExtendWith(MockitoExtension.class)
class UserJoinServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    UserDomainService userDomainService;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserJoinService userJoinService;

    // 테스트 데이터
    UserJoinCommand userJoinCommand;
    UserJoinCommand snsJoinCommand;
    User user;

    @BeforeEach
    void setup() {
        userJoinCommand = UserJoinCommand.builder()
                .userId("monty")
                .email("monty@plgrim.com")
                .password("test")
                .nickName("monty")
                .mobileNo("01040684490")
                .snsType(Sns.LOCAL)
                .snsInfo(SnsInfo.builder().build())
                .userBasic(UserBasic.builder()
                        .address("동대문구")
                        .gender(Gender.MALE)
                        .birth(LocalDate.of(1994, 3, 30))
                        .build())
                .build();
      

        snsJoinCommand = UserJoinCommand.builder()
                .userId("monty")
                .email("monty@plgrim.com")
                .nickName("monty")
                .mobileNo("0100684490")
                .snsType(Sns.KAKAO)
                .snsInfo(SnsInfo.builder()
                        .refreshToken("testtesttoken")
                        .build())
                .userBasic(UserBasic.builder()
                        .address("동대문구")
                        .gender(Gender.MALE)
                        .birth(LocalDate.of(1994, 3, 30))
                        .build())
                .build();

        user = User.builder()
                .email("monty@plgrim.com")
                .build();
    }

    @DisplayName("회원가입 성공")
    @Test
    void joinUser() {
        //  given
        given(passwordEncoder.encode(userJoinCommand.getPassword())).willReturn("encrypted password");
        given(userDomainService.checkDuplicateUserId(userJoinCommand.getUserId(), userJoinCommand.getSnsType())).willReturn(false);
        given(userDomainService.checkDuplicateEmail(userJoinCommand.getEmail(), userJoinCommand.getSnsType())).willReturn(false);
        given(userDomainService.checkDuplicateMobileNo(userJoinCommand.getMobileNo(), userJoinCommand.getSnsType())).willReturn(false);
        given(userRepository.save(any())).willReturn(user);

        //  when
        UserDTO result = userJoinService.join(userJoinCommand);

        //  then
        assertThat(result.getEmail()).isEqualTo(userJoinCommand.getEmail());
    }

    @DisplayName("회원가입 성공 - SNS 계정")
    @Test
    void joinUserSns() {
        //  given
        given(userDomainService.checkDuplicateUserId(snsJoinCommand.getUserId(), snsJoinCommand.getSnsType())).willReturn(false);
        given(userDomainService.checkDuplicateEmail(snsJoinCommand.getEmail(), snsJoinCommand.getSnsType())).willReturn(false);
        given(userDomainService.checkDuplicateMobileNo(snsJoinCommand.getMobileNo(), snsJoinCommand.getSnsType())).willReturn(false);
        given(userRepository.save(any())).willReturn(user);

        //  when
        UserDTO result = userJoinService.join(snsJoinCommand);

        //  then
        assertThat(result.getEmail()).isEqualTo(snsJoinCommand.getEmail());
    }


    @DisplayName("회원가입 실패 - userId 중복가입")
    @Test
    void joinUserFailDuplicatedUserId() {
        //  given
        given(userDomainService.checkDuplicateUserId(userJoinCommand.getUserId(), userJoinCommand.getSnsType())).willReturn(true);

        //  when
        ErrorCode error = assertThrows(UserException.class, () -> userJoinService.join(userJoinCommand)).getErrorCode();

        //  then
        assertThat(error).isEqualTo(ErrorCode.DUPLICATE_USER_ID);
    }

    @DisplayName("회원가입 실패 - email 중복가입")
    @Test
    void joinUserFailDuplicatedEmail() {
        //  given
        given(userDomainService.checkDuplicateUserId(userJoinCommand.getUserId(), userJoinCommand.getSnsType())).willReturn(false);
        given(userDomainService.checkDuplicateEmail(userJoinCommand.getEmail(), userJoinCommand.getSnsType())).willReturn(true);

        //  when
        ErrorCode error = assertThrows(UserException.class, () -> userJoinService.join(userJoinCommand)).getErrorCode();

        //  then
        assertThat(error).isEqualTo(ErrorCode.DUPLICATE_EMAIL);
    }

    @DisplayName("회원가입 실패 - mobileNo 중복가입")
    @Test
    void joinUserFailDuplicatedMobileNo() {
        //  given
        given(userDomainService.checkDuplicateUserId(userJoinCommand.getUserId(), userJoinCommand.getSnsType())).willReturn(false);
        given(userDomainService.checkDuplicateEmail(userJoinCommand.getEmail(), userJoinCommand.getSnsType())).willReturn(false);
        given(userDomainService.checkDuplicateMobileNo(userJoinCommand.getMobileNo(), userJoinCommand.getSnsType())).willReturn(true);

        //  when
        ErrorCode error = assertThrows(UserException.class, () -> userJoinService.join(userJoinCommand)).getErrorCode();

        //  then
        assertThat(error).isEqualTo(ErrorCode.DUPLICATE_MOBILE_NO);
    }
}