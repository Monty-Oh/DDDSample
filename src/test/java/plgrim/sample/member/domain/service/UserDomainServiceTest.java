package plgrim.sample.member.domain.service;

import org.junit.jupiter.api.BeforeEach;
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
import plgrim.sample.member.domain.model.valueobjects.SnsInfo;
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

    static User user;
    User otherUser;

    @BeforeEach
    void setUp() {
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

        otherUser = User.builder()
                .usrNo(2L)
                .userId("monty2")
                .email("monty@plgrim.com2")
                .build();
    }

    @DisplayName("회원 로그인 아이디 중복 체크(userId)")
    @Test
    void checkDuplicateUserId() {
        //  given
        given(userRepository.findByUserIdAndSnsType(user.getUserId(), user.getSnsType()))
                .willReturn(Optional.empty());

        //  when
        Boolean result = userDomainService.checkDuplicateUserId(user.getUserId(), user.getSnsType());

        //  then
        assertFalse(result);
    }

    @DisplayName("회원 로그인 아이디 중복 체크 실패(userId)")
    @Test
    void checkDuplicateUserIdFail() {
        //  given
        given(userRepository.findByUserIdAndSnsType(user.getUserId(), user.getSnsType()))
                .willReturn(Optional.of(user));

        //  when
        Boolean result = userDomainService.checkDuplicateUserId(user.getUserId(), user.getSnsType());

        //  then
        assertTrue(result);
    }

    @DisplayName("회원 이메일 중복 체크(email)")
    @Test
    void checkDuplicateEmailPass() {
        //  given
        given(userRepository.findByEmailAndSnsType(user.getEmail(), user.getSnsType()))
                .willReturn(Optional.empty());

        //  when
        Boolean result = userDomainService.checkDuplicateEmail(user.getEmail(), user.getSnsType());

        //  then
        assertFalse(result);
    }

    @DisplayName("회원 이메일 중복 체크(email) - 실패")
    @Test
    void checkDuplicateEmailFail() {
        //  given
        given(userRepository.findByEmailAndSnsType(user.getEmail(), user.getSnsType()))
                .willReturn(Optional.of(user));

        //  when
        Boolean result = userDomainService.checkDuplicateEmail(user.getEmail(), user.getSnsType());

        //  then
        assertTrue(result);
    }

    @DisplayName("회원 전화번호 중복 체크(mobileNo) - 통과")
    @Test
    void checkDuplicateMobileNo() {
        //  given
        given(userRepository.findByMobileNoAndSnsType(user.getMobileNo(), user.getSnsType()))
                .willReturn(Optional.empty());

        //  when
        Boolean result = userDomainService.checkDuplicateMobileNo(user.getMobileNo(), user.getSnsType());

        //  then
        assertFalse(result);
    }

    @DisplayName("회원 전화번호 중복 체크(mobileNo) - 실패")
    @Test
    void checkDuplicateMobileNoFail() {
        //  given
        given(userRepository.findByMobileNoAndSnsType(user.getMobileNo(), user.getSnsType()))
                .willReturn(Optional.of(user));

        //  when
        Boolean result = userDomainService.checkDuplicateMobileNo(user.getMobileNo(), user.getSnsType());

        //  then
        assertTrue(result);
    }


    /**
     * 중복체크 성공 케이스 모음
     */
    static Stream<Arguments> getUserCheckDuplicateCase() {
        return Stream.of(
                Arguments.arguments(user),          //  사용은 하는데 자기 자신일 때
                Arguments.arguments((User) null)    //  아무도 사용 안할 때
        );
    }

    @DisplayName("회원 이메일 중복 체크(email, snsType, userId) - 통과")
    @ParameterizedTest(name = "중복 체크 통과, findByEmail return: {0}")
    @MethodSource("getUserCheckDuplicateCase")
    void checkDuplicateEmailExceptOwn(User expectedReturn) {
        //  given
        given(userRepository.findByEmailAndSnsType(user.getEmail(), user.getSnsType())).willReturn(Optional.ofNullable(expectedReturn));

        //  when
        Boolean result = userDomainService.checkDuplicateEmailExceptOwn(user.getEmail(), user.getSnsType(), user.getUserId());

        //  then
        assertFalse(result);
    }

    @DisplayName("회원 전화번호 중복 체크(mobileNo, snsType, userId) - 통과")
    @ParameterizedTest(name = "중복 체크 통과, findByMobileNo return: {0}")
    @MethodSource("getUserCheckDuplicateCase")
    void checkDuplicatePhoneNumberAndUsrNoOwnPhoneNumber(User expectedReturn) {
        //  given
        given(userRepository.findByMobileNoAndSnsType(user.getMobileNo(), user.getSnsType())).willReturn(Optional.ofNullable(expectedReturn));

        //  when
        Boolean result = userDomainService.checkDuplicatePhoneNumberExceptOwn(user.getMobileNo(), user.getSnsType(), user.getUserId());

        //  then
        assertFalse(result);
    }

    @DisplayName("회원 이메일 중복 체크(email, snsType, userId) - 실패, 이미 사용함")
    @Test
    void checkDuplicateEmailAndUsrNoFail() {
        //  given
        given(userRepository.findByEmailAndSnsType(user.getEmail(), user.getSnsType())).willReturn(Optional.ofNullable(otherUser));

        //  when
        Boolean result = userDomainService.checkDuplicateEmailExceptOwn(user.getEmail(), user.getSnsType(), user.getUserId());

        //  then
        assertTrue(result);
    }

    @DisplayName("회원 전화번호 중복 체크(mobileNo, snsType, userId) - 실패, 이미 사용함")
    @Test
    void checkDuplicatePhoneNumberAndUsrNoFail() {
        //  given
        given(userRepository.findByMobileNoAndSnsType(user.getMobileNo(), user.getSnsType())).willReturn(Optional.ofNullable(otherUser));

        //  when
        Boolean result = userDomainService.checkDuplicatePhoneNumberExceptOwn(user.getMobileNo(), user.getSnsType(), user.getUserId());

        //  then
        assertTrue(result);
    }
}