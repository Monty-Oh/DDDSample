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
import plgrim.sample.member.controller.dto.user.UserDTO;
import plgrim.sample.member.controller.dto.user.UserModifyDTO;
import plgrim.sample.member.domain.model.aggregates.User;
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
    SHA256 sha256;

    @Mock
    UserDomainService userDomainService;

    @InjectMocks
    UserModifyService userModifyService;

    // 테스트 데이터
    User user;

    UserModifyDTO userModifyDTO;

    @BeforeEach
    void setup() {
        user = User.builder()
                .usrNo(1L)
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

        userModifyDTO = UserModifyDTO.builder()
                .email("monty@plgrim.com")
                .password("123123213")
                .phoneNumber("01080140922")
                .address("동탄")
                .gender(Gender.MALE)
                .birth(LocalDate.of(2021, 9, 9))
                .snsType(Sns.GOOGLE)
                .build();
    }

    @DisplayName("회원정보 수정")
    @Test
    void modify() {
        //  given
        given(userRepository.findById(userModifyDTO.getUsrNo())).willReturn(Optional.of(user));
        given(userDomainService.checkDuplicateEmail(userModifyDTO.getEmail(), userModifyDTO.getUsrNo())).willReturn(false);
        given(userDomainService.checkDuplicatePhoneNumber(userModifyDTO.getPhoneNumber(), userModifyDTO.getUsrNo())).willReturn(false);
        given(userRepository.save(any())).willReturn(user);
        given(sha256.encrypt(userModifyDTO.getPassword())).willReturn("encrypt password");

        //  when
        UserDTO result = userModifyService.modify(userModifyDTO);

        //  then
        assertThat(result.getEmail()).isEqualTo(userModifyDTO.getEmail());
    }

    @DisplayName("회원정보 수정 실패 - 없는 회원")
    @Test
    void modifyFailNotUserFound() {
        //  given
        given(userRepository.findById(userModifyDTO.getUsrNo())).willReturn(Optional.empty());

        //  when
        ErrorCode error = assertThrows(UserException.class, () -> userModifyService.modify(userModifyDTO)).getErrorCode();

        //  then
        assertThat(error).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
    }

    @DisplayName("회원정보 수정 실패 - 이메일 중복")
    @Test
    void modifyFailDuplicatedEmail() {
        //  given
        given(userRepository.findById(userModifyDTO.getUsrNo())).willReturn(Optional.of(user));
        given(userDomainService.checkDuplicateEmail(userModifyDTO.getEmail(), userModifyDTO.getUsrNo())).willReturn(true);

        //  when
        ErrorCode error = assertThrows(UserException.class, () -> userModifyService.modify(userModifyDTO)).getErrorCode();

        //  then
        assertThat(error).isEqualTo(ErrorCode.DUPLICATE_ID);
    }

    @DisplayName("회원정보 수정 실패 - 전화번호 중복")
    @Test
    void modifyFailDuplicatedPhoneNumber() {
        //  given
        given(userRepository.findById(userModifyDTO.getUsrNo())).willReturn(Optional.of(user));
        given(userDomainService.checkDuplicateEmail(userModifyDTO.getEmail(), userModifyDTO.getUsrNo())).willReturn(false);
        given(userDomainService.checkDuplicatePhoneNumber(userModifyDTO.getPhoneNumber(), userModifyDTO.getUsrNo())).willReturn(true);

        //  when
        ErrorCode error = assertThrows(UserException.class, () -> userModifyService.modify(userModifyDTO)).getErrorCode();

        //  then
        assertThat(error).isEqualTo(ErrorCode.DUPLICATE_PHONE_NUMBER);
    }

    @DisplayName("회원정보 삭제")
    @Test
    void delete() {
        //  given
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        //  when    //  then
        assertDoesNotThrow(() -> userModifyService.delete(1L));
    }

    @DisplayName("회원정보 삭제 실패 - 없는 회원")
    @Test
    void deleteFailNotUserFound() {
        //  given
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        //  when
        ErrorCode error = assertThrows(UserException.class, () -> userModifyService.delete(1L)).getErrorCode();

        //  then
        assertThat(error).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
    }
}