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
import plgrim.sample.member.domain.model.commands.UserModifyCommand;
import plgrim.sample.member.domain.model.valueobjects.SnsInfo;
import plgrim.sample.member.domain.model.valueobjects.UserBasic;
import plgrim.sample.member.domain.service.UserDomainService;
import plgrim.sample.member.infrastructure.repository.UserJPARepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("UserModifyService 테스트")
@ExtendWith(MockitoExtension.class)
class UserModifyServiceTest {
    @Mock
    UserJPARepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    UserDomainService userDomainService;

    @InjectMocks
    UserModifyService userModifyService;

    // 테스트 데이터
    User user;
    UserModifyCommand userModifyCommand;

    @BeforeEach
    void setup() {
        user = User.builder()
                .usrNo(1L)
                .userId("monty")
                .email("monty@plgrim.com")
                .password("12345")
                .nickName("monty")
                .mobileNo("01040684490")
                .snsType(Sns.LOCAL)
                .snsInfo(SnsInfo.builder().build())
                .userBasic(UserBasic.builder()
                        .address("dongdaemungu")
                        .gender(Gender.MALE)
                        .birth(LocalDate.of(1994, 3, 30))
                        .build())
                .build();

        userModifyCommand = UserModifyCommand.builder()
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
    }

    @DisplayName("회원정보 수정")
    @Test
    void modify() {
        //  given
        given(userRepository.findByUserId(userModifyCommand.getUserId())).willReturn(Optional.of(user));
        given(userDomainService.checkDuplicateEmailExceptOwn(userModifyCommand.getEmail(), userModifyCommand.getSnsType(), userModifyCommand.getUserId())).willReturn(false);
        given(userDomainService.checkDuplicatePhoneNumberExceptOwn(userModifyCommand.getMobileNo(), userModifyCommand.getSnsType(), userModifyCommand.getUserId())).willReturn(false);
        given(userRepository.save(any())).willReturn(user);
        given(passwordEncoder.encode(userModifyCommand.getPassword())).willReturn("encrypt password");

        //  when
        UserDTO result = userModifyService.modify(userModifyCommand);

        //  then
        assertThat(result.getEmail()).isEqualTo(userModifyCommand.getEmail());
    }

    @DisplayName("회원정보 수정 실패 - 없는 회원")
    @Test
    void modifyFailNotUserFound() {
        //  given
        given(userRepository.findByUserId(userModifyCommand.getUserId())).willReturn(Optional.empty());

        //  when
        ErrorCode error = assertThrows(UserException.class, () -> userModifyService.modify(userModifyCommand)).getErrorCode();

        //  then
        assertThat(error).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @DisplayName("회원정보 수정 실패 - 이메일 중복")
    @Test
    void modifyFailDuplicatedEmail() {
        //  given
        given(userRepository.findByUserId(userModifyCommand.getUserId())).willReturn(Optional.of(user));
        given(userDomainService.checkDuplicateEmailExceptOwn(userModifyCommand.getEmail(), userModifyCommand.getSnsType(), userModifyCommand.getUserId())).willReturn(true);

        //  when
        ErrorCode error = assertThrows(UserException.class, () -> userModifyService.modify(userModifyCommand)).getErrorCode();

        //  then
        assertThat(error).isEqualTo(ErrorCode.DUPLICATE_EMAIL);
    }

    @DisplayName("회원정보 수정 실패 - 전화번호 중복")
    @Test
    void modifyFailDuplicatedPhoneNumber() {
        //  given
        given(userRepository.findByUserId(userModifyCommand.getUserId())).willReturn(Optional.of(user));
        given(userDomainService.checkDuplicateEmailExceptOwn(userModifyCommand.getEmail(), userModifyCommand.getSnsType(), userModifyCommand.getUserId())).willReturn(false);
        given(userDomainService.checkDuplicatePhoneNumberExceptOwn(userModifyCommand.getMobileNo(), userModifyCommand.getSnsType(), userModifyCommand.getUserId())).willReturn(true);
        //  when
        ErrorCode error = assertThrows(UserException.class, () -> userModifyService.modify(userModifyCommand)).getErrorCode();

        //  then
        assertThat(error).isEqualTo(ErrorCode.DUPLICATE_MOBILE_NO);
    }

    @DisplayName("회원정보 삭제")
    @Test
    void delete() {
        //  given
        given(userRepository.findByUserIdAndSnsType(user.getUserId(), user.getSnsType()))
                .willReturn(Optional.of(user));

        //  when    //  then
        assertDoesNotThrow(() -> userModifyService.delete(user.getUserId(), user.getSnsType().getValue()));
    }

    @DisplayName("회원정보 삭제 실패 - 없는 회원")
    @Test
    void deleteFailNotUserFound() {
        //  given
        given(userRepository.findByUserIdAndSnsType(user.getUserId(), user.getSnsType()))
                .willReturn(Optional.empty());

        //  when
        ErrorCode error = assertThrows(UserException.class, () ->  userModifyService.delete(user.getUserId(), user.getSnsType().getValue())).getErrorCode();

        //  then
        assertThat(error).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }
}