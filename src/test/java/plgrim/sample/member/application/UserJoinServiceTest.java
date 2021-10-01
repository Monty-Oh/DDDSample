package plgrim.sample.member.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import plgrim.sample.common.SHA256;
import plgrim.sample.common.enums.ErrorCode;
import plgrim.sample.common.enums.Gender;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.common.exceptions.UserException;
import plgrim.sample.member.domain.model.aggregates.User;
import plgrim.sample.member.domain.model.commands.UserJoinCommand;
import plgrim.sample.member.domain.model.valueobjects.UserBasic;
import plgrim.sample.member.domain.service.UserDomainService;
import plgrim.sample.member.domain.service.UserRepository;

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
    SHA256 sha256;

    @InjectMocks
    UserJoinService userJoinService;

    // 테스트 데이터
    UserJoinCommand userJoinCommand;
    User user;

    @BeforeEach
    void setup() {
        userJoinCommand = UserJoinCommand.builder()
                .id("monty@plgrim.com")
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
                .id("monty@plgrim.com")
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
        given(sha256.encrypt(userJoinCommand.getPassword())).willReturn("encrypted password");
        given(userDomainService.checkDuplicateId(userJoinCommand.getId())).willReturn(false);
        given(userDomainService.checkDuplicatePhoneNumber(userJoinCommand.getPhoneNumber())).willReturn(false);
        given(userRepository.save(any())).willReturn(user);

        //  when
        String id = userJoinService.join(userJoinCommand);

        //  then
        assertThat(id).isEqualTo(userJoinCommand.getId());
    }

    @DisplayName("회원가입 실패 - id 중복가입")
    @Test
    void joinUserFailDuplicatedId() {
        //  given
        given(userDomainService.checkDuplicateId(userJoinCommand.getId()))
                .willReturn(true)
                .willThrow(new UserException(ErrorCode.DUPLICATE_ID));

        //  when    //  then
        assertThrows(UserException.class, () -> userJoinService.join(userJoinCommand));
    }

    @DisplayName("회원가입 실패 - PhoneNum 중복가입")
    @Test
    void joinUserFailDuplicatedPhoneNum() {
        given(userDomainService.checkDuplicateId(userJoinCommand.getId())).willReturn(false);
        given(userDomainService.checkDuplicatePhoneNumber(userJoinCommand.getPhoneNumber()))
                .willReturn(true)
                .willThrow(new UserException(ErrorCode.DUPLICATE_PHONE_NUMBER));

        //  when    //  then
        assertThrows(UserException.class, () -> userJoinService.join(userJoinCommand));
    }
}