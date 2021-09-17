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

    @DisplayName("유저조회(Id)")
    @Test
    void findUserById() {
        String userId = userJoinService.join(userJoinCommand);                  // 가입 후 ID 반환받음
        UserFindByIdDTO userFindByIdDTO = UserFindByIdDTO.builder()         // 이미 저장된 userId로 조회 DTO 생성
                .id(userId)
                .build();
        UserDTO userDto = userFindService.findUserById(userFindByIdDTO);    // 조회 실시
        assertThat(userDto.getId()).isEqualTo("monty@plgrim.com");          // ID 동일한지 체크
    }

    @DisplayName("유저조회(ID) 실패 - UserNotFound")
    @Test
    void 조회_실패() {
        UserFindByIdDTO userFindByIdDTO = UserFindByIdDTO.builder()         // 테스트 DTO
                .id("notExist")
                .build();
        assertThrows(UserException.class,
                () -> userFindService.findUserById(userFindByIdDTO));       // 조회 실패해야함. 존재하지 않는 데이터
    }

    @DisplayName("유저 목록 조회")
    @Test
    void findUsers() {
        userJoinService.join(userJoinCommand);              // 테스트 데이터 저장
        List<User> users = userFindService.findUsers();
        System.out.println(users);
    }
}