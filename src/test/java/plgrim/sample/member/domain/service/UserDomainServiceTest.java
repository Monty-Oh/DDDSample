package plgrim.sample.member.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import plgrim.sample.common.enums.Gender;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.member.domain.model.aggregates.User;
import plgrim.sample.member.domain.model.valueobjects.UserBasic;
import plgrim.sample.member.infrastructure.repository.UserJPARepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

@DisplayName("UserDomainService 테스트")
@ExtendWith(MockitoExtension.class)
class UserDomainServiceTest {
    @Mock
    UserJPARepository userRepository;

    @InjectMocks
    UserDomainService userDomainService;

    User user;
    User otherUser;

    @BeforeEach
    void setUp() {
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

        otherUser = User.builder()
                .usrNo(2L)
                .email("monty@plgrim.com")
                .build();
    }

    @DisplayName("회원 이메일 중복 체크(email) - 통과")
    @Test
    void checkDuplicateEmailPass() {
        //  given
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.empty());

        //  when
        Boolean result = userDomainService.checkDuplicateEmail(user.getEmail());

        //  then
        assertFalse(result);
    }

    @DisplayName("회원 이메일 중복 체크(email) - 실패")
    @Test
    void checkDuplicateEmailFail() {
        //  given
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

        //  when
        Boolean result = userDomainService.checkDuplicateEmail(user.getEmail());

        //  then
        assertTrue(result);
    }

    @DisplayName("회원 이메일 중복 체크(email, usrNo) - 통과, 본인 이메일")
    @Test
    void checkDuplicateEmailIncludeUsrNoPass() {
        //  given
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

        //  when
        Boolean result = userDomainService.checkDuplicateEmail(user.getEmail(), user.getUsrNo());

        //  then
        assertFalse(result);
    }

    @DisplayName("회원 이메일 중복 체크(email, usrNo) - 통과, 아무도 사용 안함")
    @Test
    void checkDuplicateEmailExcludeUsrNoPass() {
        //  given
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.empty());

        //  when
        Boolean result = userDomainService.checkDuplicateEmail(user.getEmail(), user.getUsrNo());

        //  then
        assertFalse(result);
    }

    @DisplayName("회원 이메일 중복 체크(email, usrNo) - 이미 사용함")
    @Test
    void checkDuplicateEmailUsrNoFail() {
        //  given
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(otherUser));

        //  when
        Boolean result = userDomainService.checkDuplicateEmail(user.getEmail(), user.getUsrNo());

        //  then
        assertTrue(result);
    }

    @DisplayName("회원 전화번호 중복 체크(phoneNumber) - 통과")
    @Test
    void checkDuplicatePhoneNumberPass() {
        //  given
        given(userRepository.findByPhoneNumber(user.getPhoneNumber())).willReturn(Optional.of(user));

        //  when
        Boolean result = userDomainService.checkDuplicatePhoneNumber(user.getPhoneNumber());

        //  then
        assertTrue(result);
    }

    @DisplayName("회원 전화번호 중복 체크(phoneNumber) - 실패")
    @Test
    void checkDuplicatePhoneNumberFail() {
        //  given
        given(userRepository.findByPhoneNumber(user.getPhoneNumber())).willReturn(Optional.empty());

        //  when
        Boolean result = userDomainService.checkDuplicatePhoneNumber(user.getPhoneNumber());

        //  then
        assertFalse(result);
    }
}