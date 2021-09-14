package plgrim.sample.member.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import plgrim.sample.common.enums.Gender;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.common.exception.UserNotFoundException;
import plgrim.sample.member.controller.dto.user.UserDTO;
import plgrim.sample.member.controller.dto.user.UserFindByIdDTO;
import plgrim.sample.member.controller.dto.user.UserJoinDTO;
import plgrim.sample.member.controller.dto.user.UserModifyDTO;
import plgrim.sample.member.domain.model.commands.UserJoinCommand;
import plgrim.sample.member.domain.model.vo.UserBasic;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("UserModifyService 테스트")
@Transactional
@SpringBootTest
class UserModifyServiceTest {
    @Autowired
    UserModifyService userModifyService;
    @Autowired
    UserJoinService userJoinService;
    @Autowired
    UserFindService userFindService;

    // 테스트 데이터
    UserJoinCommand userJoinCommand = UserJoinCommand.builder()
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


    @DisplayName("회원정보 수정")
    @Test
    void modify() {
        userJoinService.join(userJoinCommand);                              // 가입 후 수정 시도

        UserModifyDTO userModifyDTO = UserModifyDTO.builder()
                .id("monty@plgrim.com")
                .password("123123213")
                .phoneNumber("01080140922")
                .address("동탄")
                .gender(Gender.MALE)
                .birth(LocalDate.of(2021, 9, 9))
                .SnsType(Sns.GOOGLE)
                .build();

        String userId = userModifyService.modify(userModifyDTO);
        UserFindByIdDTO userFindByIdDto = UserFindByIdDTO.builder()     // 해당 id로 다시 조회해서 비교한다.
                .id(userId)
                .build();
        UserDTO userDto = userFindService.findUserById(userFindByIdDto);
        assertThat(userDto.getPhoneNumber()).isEqualTo("01080140922");
    }

    @DisplayName("회원정보 수정 실패 - 없는 회원")
    @Test
    void 수정실패_없는회원() {
        UserModifyDTO userModifyDTO = UserModifyDTO.builder()
                .id("0")
                .password("123123213")
                .phoneNumber("01080140922")
                .address("동탄")
                .gender(Gender.MALE)
                .birth(LocalDate.of(2021, 9, 9))
                .SnsType(Sns.GOOGLE)
                .build();

        assertThrows(UserNotFoundException.class, () -> userModifyService.modify(userModifyDTO));
    }
}