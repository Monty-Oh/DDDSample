package plgrim.sample.member.domain.service;

import org.assertj.core.api.Assertions;
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

    /**
     * UserDomainService 호출 메소드 파라미터가 1개일 때
     * */
    static Stream<Arguments> getOptionalSource() {
        return Stream.of(
                //  통과
                //      아무도 사용하지 않을 때
                Arguments.arguments(Optional.empty(), false),
                //  미통과
                //      누군가 사용하고 있을 때
                Arguments.arguments(Optional.of(user), true)
        );
    }

    /**
     * UserDomainService 호출 메소드 파라미터가 2개일 때 (usrNo 포함)
     * */
    static Stream<Arguments> getOptionalSourceUsingUsrNo() {
        return Stream.of(
                //  통과
                //      아무도 사용하지 않을 때
                Arguments.arguments(Optional.empty(), false),
                //      누군가 사용하는데 그게 자기 자신일 때
                Arguments.arguments(Optional.of(user), false),
                //  미통과
                //      자기 자신이 아닌 누군가가 사용할 때
                Arguments.arguments(Optional.of(otherUser), true)
        );
    }

//    @DisplayName("회원 이메일 중복 체크(email)")
//    @Test
//    void checkDuplicateEmailPass() {
//        //  given
//        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.empty());
//
//        //  when
//        Boolean result = userDomainService.checkDuplicateEmail(user.getEmail());
//
//        //  then
//        assertFalse(result);
//    }
//
//    @DisplayName("회원 이메일 중복 체크(email) - 실패")
//    @Test
//    void checkDuplicateEmailFail() {
//        //  given
//        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
//
//        //  when
//        Boolean result = userDomainService.checkDuplicateEmail(user.getEmail());
//
//        //  then
//        assertTrue(result);
//    }

    @DisplayName("회원 이메일 중복 체크(email)")
    @ParameterizedTest(name = "userRepository.findByEmail 반환값: {0}, userDomainService.checkDuplicateEmail 예상 반환 값: {1}")
    @MethodSource("getOptionalSource")
    void checkDuplicatedEmail(Optional<User> expectedReturn, Boolean expected) {
        //  given
        given(userRepository.findByEmail(user.getEmail())).willReturn(expectedReturn);

        //  when
        Boolean result = userDomainService.checkDuplicateEmail(user.getEmail());

        //  then
        Assertions.assertThat(expected).isEqualTo(result);
    }

//    @DisplayName("회원 이메일 중복 체크(email, usrNo) - 통과, 본인 이메일")
//    @Test
//    void checkDuplicateEmailIncludeUsrNoPass() {
//        //  given
//        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
//
//        //  when
//        Boolean result = userDomainService.checkDuplicateEmail(user.getEmail(), user.getUsrNo());
//
//        //  then
//        assertFalse(result);
//    }
//
//    @DisplayName("회원 이메일 중복 체크(email, usrNo) - 통과, 아무도 사용 안함")
//    @Test
//    void checkDuplicateEmailExcludeUsrNoPass() {
//        //  given
//        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.empty());
//
//        //  when
//        Boolean result = userDomainService.checkDuplicateEmail(user.getEmail(), user.getUsrNo());
//
//        //  then
//        assertFalse(result);
//    }
//
//    @DisplayName("회원 이메일 중복 체크(email, usrNo) - 이미 사용함")
//    @Test
//    void checkDuplicateEmailUsrNoFail() {
//        //  given
//        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(otherUser));
//
//        //  when
//        Boolean result = userDomainService.checkDuplicateEmail(user.getEmail(), user.getUsrNo());
//
//        //  then
//        assertTrue(result);
//    }

    @DisplayName("회원 이메일 중복 체크(email, usrNo)")
    @ParameterizedTest(name = "userRepository.findByEmail 반환값: {0}, userDomainService.checkDuplicateEmail 예상 반환 값: {1}")
    @MethodSource("getOptionalSourceUsingUsrNo")
    void checkDuplicateEmailUsingUsrNo(Optional<User> expectedReturn, Boolean expected) {
        //  given
        given(userRepository.findByEmail(user.getEmail())).willReturn(expectedReturn);

        //  when
        Boolean result = userDomainService.checkDuplicateEmail(user.getEmail(), user.getUsrNo());

        //  then
        Assertions.assertThat(expected).isEqualTo(result);
    }

//    @DisplayName("회원 전화번호 중복 체크(phoneNumber) - 통과")
//    @Test
//    void checkDuplicatePhoneNumberPass() {
//        //  given
//        given(userRepository.findByPhoneNumber(user.getPhoneNumber())).willReturn(Optional.of(user));
//
//        //  when
//        Boolean result = userDomainService.checkDuplicatePhoneNumber(user.getPhoneNumber());
//
//        //  then
//        assertTrue(result);
//    }
//
//    @DisplayName("회원 전화번호 중복 체크(phoneNumber) - 실패")
//    @Test
//    void checkDuplicatePhoneNumberFail() {
//        //  given
//        given(userRepository.findByPhoneNumber(user.getPhoneNumber())).willReturn(Optional.empty());
//
//        //  when
//        Boolean result = userDomainService.checkDuplicatePhoneNumber(user.getPhoneNumber());
//
//        //  then
//        assertFalse(result);
//    }

    @DisplayName("회원 전화번호 중복 체크(phoneNumber)")
    @ParameterizedTest(name = "userRepository.findByPhoneNumber 반환값: {0}, userDomainService.checkDuplicatePhoneNumber 예상 반환 값: {1}")
    @MethodSource("getOptionalSource")
    void checkDuplicatePhoneNumber(Optional<User> expectedReturn, Boolean expected) {
        //  given
        given(userRepository.findByPhoneNumber(user.getPhoneNumber())).willReturn(expectedReturn);

        //  when
        Boolean result = userDomainService.checkDuplicatePhoneNumber(user.getPhoneNumber());

        //  then
        Assertions.assertThat(expected).isEqualTo(result);
    }

    @DisplayName("회원 전화번호 중복 체크(phoneNumber, usrNo)")
    @ParameterizedTest(name = "userRepository.findByPhoneNumber 반환값: {0}, userDomainService.checkDuplicatePhoneNumber 예상 반환 값: {1}")
    @MethodSource("getOptionalSourceUsingUsrNo")
    void checkDuplicatePhoneNumberUsingUsrNo(Optional<User> expectedReturn, Boolean expected) {
        //  given
        given(userRepository.findByPhoneNumber(user.getPhoneNumber())).willReturn(expectedReturn);

        //  when
        Boolean result = userDomainService.checkDuplicatePhoneNumber(user.getPhoneNumber(), user.getUsrNo());

        //  then
        Assertions.assertThat(expected).isEqualTo(result);
    }
}