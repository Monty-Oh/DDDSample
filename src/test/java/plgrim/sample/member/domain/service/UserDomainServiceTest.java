package plgrim.sample.member.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import java.util.stream.Stream;

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

    static User user = User.builder()
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
    static User otherUser = User.builder()
            .usrNo(2L)
            .email("monty@plgrim.com")
            .build();

    @DisplayName("회원 이메일 중복 체크(email)")
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

    @DisplayName("회원 전화번호 중복 체크(phoneNumber) - 통과")
    @Test
    void checkDuplicatePhoneNumber() {
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


    /**
     * 중복체크 성공 케이스 모음
     * Optional 해결하기
     */
    static Stream<Arguments> getUserCheckDuplicateCase() {
        return Stream.of(
                //  사용은 하는데 자기 자신일 때
                Arguments.arguments(user),
                //  아무도 사용 안할 때
                Arguments.arguments((User) null)
        );
    }

    @DisplayName("회원 이메일 중복 체크(email, usrNo) - 통과")
    @ParameterizedTest(name = "중복 체크 통과, findByEmail return: {0}")
    @MethodSource("getUserCheckDuplicateCase")
    void checkDuplicateEmailAndUsrNoOwnEmail(User expectedReturn) {
        //  given
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.ofNullable(expectedReturn));

        //  when
        Boolean result = userDomainService.checkDuplicateEmailExceptOwn(user.getEmail(), user.getUsrNo());

        //  then
        assertFalse(result);
    }

    @DisplayName("회원 전화번호 중복 체크(phoneNumber, usrNo) - 통과")
    @ParameterizedTest(name = "중복 체크 통과, findByPhoneNumber return: {0}")
    @MethodSource("getUserCheckDuplicateCase")
    void checkDuplicatePhoneNumberAndUsrNoOwnPhoneNumber(User expectedReturn) {
        //  given
        given(userRepository.findByPhoneNumber(user.getPhoneNumber())).willReturn(Optional.ofNullable(expectedReturn));

        //  when
        Boolean result = userDomainService.checkDuplicatePhoneNumberExceptOwn(user.getPhoneNumber(), user.getUsrNo());

        //  then
        assertFalse(result);
    }

    @DisplayName("회원 이메일 중복 체크(email, usrNo) - 실패, 이미 사용함")
    @Test
    void checkDuplicateEmailAndUsrNoFail() {
        //  given
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(otherUser));

        //  when
        Boolean result = userDomainService.checkDuplicateEmailExceptOwn(user.getEmail(), user.getUsrNo());

        //  then
        assertTrue(result);
    }

    @DisplayName("회원 전화번호 중복 체크(phoneNumber, usrNo) - 실패, 이미 사용함")
    @Test
    void checkDuplicatePhoneNumberAndUsrNoFail() {
        //  given
        given(userRepository.findByPhoneNumber(user.getPhoneNumber())).willReturn(Optional.of(otherUser));

        //  when
        Boolean result = userDomainService.checkDuplicatePhoneNumberExceptOwn(user.getPhoneNumber(), user.getUsrNo());

        //  then
        assertTrue(result);
    }
}