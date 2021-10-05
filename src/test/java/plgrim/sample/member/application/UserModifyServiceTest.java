package plgrim.sample.member.application;

import org.assertj.core.api.Assertions;
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
import plgrim.sample.member.controller.dto.user.UserModifyDTO;
import plgrim.sample.member.domain.model.aggregates.User;
import plgrim.sample.member.domain.model.valueobjects.UserBasic;
import plgrim.sample.member.domain.service.UserRepository;
import plgrim.sample.member.infrastructure.repository.UserJPARepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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

    @InjectMocks
    UserModifyService userModifyService;

    // 테스트 데이터
    User user = User.builder()
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

    UserModifyDTO userModifyDTO = UserModifyDTO.builder()
                .email("monty@plgrim.com")
                .password("123123213")
                .phoneNumber("01080140922")
                .address("동탄")
                .gender(Gender.MALE)
                .birth(LocalDate.of(2021, 9, 9))
                .SnsType(Sns.GOOGLE)
                .build();

    @DisplayName("회원정보 수정")
    @Test
    void modify() {
        //  given
        given(userRepository.findByEmail(userModifyDTO.getEmail())).willReturn(Optional.of(user));
        given(userRepository.save(any())).willReturn(null);
        given(sha256.encrypt(userModifyDTO.getPassword())).willReturn("encrypt password");

        //  when
        String id = userModifyService.modify(userModifyDTO);

        //  then
        assertThat(id).isEqualTo(userModifyDTO.getEmail());
    }

    @DisplayName("회원정보 수정 실패 - 없는 회원")
    @Test
    void modifyFailNotUserFound() {
        //  given
        given(userRepository.findByEmail(userModifyDTO.getEmail()))
//                .willReturn(Optional.empty()) given엔 필요한것만 쓰자
                .willThrow(new UserException(ErrorCode.MEMBER_NOT_FOUND));

        //  when    //  then
        assertThrows(UserException.class, () -> userModifyService.modify(userModifyDTO));
    }
}