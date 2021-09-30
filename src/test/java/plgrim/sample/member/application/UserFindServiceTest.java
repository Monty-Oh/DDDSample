package plgrim.sample.member.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import plgrim.sample.common.enums.Gender;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.common.exceptions.UserException;
import plgrim.sample.member.controller.dto.user.UserDTO;
import plgrim.sample.member.controller.dto.user.UserFindByIdDTO;
import plgrim.sample.member.domain.model.commands.UserJoinCommand;
import plgrim.sample.member.domain.model.aggregates.User;
import plgrim.sample.member.domain.model.valueobjects.UserBasic;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("UserFindService 테스트")
@Transactional
@SpringBootTest
class UserFindServiceTest {
    @Autowired
    UserFindService userFindService;

    @Autowired
    UserJoinService userJoinService;                                        // 회원 가입 목적

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
    UserJoinCommand userJoinCommand2 = UserJoinCommand.builder()
            .id("monty@plgrim.com2")
            .password("test")
            .phoneNumber("01040684491")
            .userBasic(UserBasic.builder()
                    .address("동대문구")
                    .gender(Gender.MALE)
                    .birth(LocalDate.of(1994, 3, 30))
                    .snsType(Sns.LOCAL)
                    .build())
            .build();
    UserJoinCommand userJoinCommand3 = UserJoinCommand.builder()
            .id("monty@plgrim.com3")
            .password("test")
            .phoneNumber("01040684492")
            .userBasic(UserBasic.builder()
                    .address("동대문구")
                    .gender(Gender.MALE)
                    .birth(LocalDate.of(1994, 3, 30))
                    .snsType(Sns.LOCAL)
                    .build())
            .build();

    @DisplayName("유저조회(Id)")
    @Test
    void findUserById() {
        //  given
        //  가입 후 ID 반환받음
        String userId = userJoinService.join(userJoinCommand);

        //  when
        //  이미 저장된 userId로 조회 DTO 생성
        UserFindByIdDTO userFindByIdDTO = UserFindByIdDTO.builder()
                .id(userId)
                .build();
        //  조회 실시
        UserDTO userDto = userFindService.findUserById(userFindByIdDTO);

        //  then
        //  ID 동일한지 체크
        assertThat(userDto.getId()).isEqualTo("monty@plgrim.com");
    }

    @DisplayName("유저조회(ID) 실패 - UserNotFound")
    @Test
    void 조회_실패() {
        // given
        UserFindByIdDTO userFindByIdDTO = UserFindByIdDTO.builder()         // 테스트 DTO
                .id("notExist")
                .build();

        // when, then
        assertThrows(UserException.class,
                () -> userFindService.findUserById(userFindByIdDTO));       // 조회 실패해야함. 존재하지 않는 데이터
    }

    @DisplayName("유저 목록 조회")
    @Test
    void findUsers() {
        // given
        // 테스트 데이터 저장
        userJoinService.join(userJoinCommand);
        userJoinService.join(userJoinCommand2);
        userJoinService.join(userJoinCommand3);

        // when
        List<User> users = userFindService.findUsers();

        // then
        assertThat(users.size()).isEqualTo(3);
    }

    @DisplayName("유저 목록 조회 - page")
    @Test
    void findUsersPage() {
        //  given
        userJoinService.join(userJoinCommand);
        userJoinService.join(userJoinCommand2);
        userJoinService.join(userJoinCommand3);

        //  when
        List<User> users = userFindService.findUsers(0, 2);

        // then
        assertThat(users.size()).isEqualTo(2);
    }
}