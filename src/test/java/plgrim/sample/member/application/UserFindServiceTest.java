package plgrim.sample.member.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import plgrim.sample.common.enums.ErrorCode;
import plgrim.sample.common.enums.Gender;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.common.exceptions.UserException;
import plgrim.sample.member.controller.dto.user.UserDTO;
import plgrim.sample.member.domain.model.aggregates.User;
import plgrim.sample.member.domain.model.entities.SnsInfo;
import plgrim.sample.member.domain.model.valueobjects.UserBasic;
import plgrim.sample.member.infrastructure.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@DisplayName("UserFindService 테스트")
@ExtendWith(MockitoExtension.class)
class UserFindServiceTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserFindService userFindService;

    User user;
    User user2;
    User user3;

    @BeforeEach
    void setup() {
        user = User.builder()
                .usrNo(1L)
                .userId("monty")
                .email("monty@plgrim.com")
                .password("123456")
                .nickName("monty")
                .mobileNo("01040684490")
                .snsType(Sns.LOCAL)
                .snsInfo(SnsInfo.builder().build())
                .userBasic(UserBasic.builder()
                        .address("동대문구")
                        .gender(Gender.MALE)
                        .birth(LocalDate.of(1994, 3, 30))
                        .build())
                .build();
        user2 = User.builder()
                .usrNo(2L)
                .userId("monty2")
                .email("monty2@plgrim.com")
                .password("123456")
                .nickName("monty2")
                .mobileNo("01040684491")
                .snsType(Sns.LOCAL)
                .snsInfo(SnsInfo.builder().build())
                .userBasic(UserBasic.builder()
                        .address("동대문구")
                        .gender(Gender.MALE)
                        .birth(LocalDate.of(1994, 3, 30))
                        .build())
                .build();
        user3 = User.builder()
                .usrNo(3L)
                .userId("monty3")
                .email("monty3@plgrim.com")
                .password("123456")
                .nickName("monty3")
                .mobileNo("01040684492")
                .snsType(Sns.LOCAL)
                .snsInfo(SnsInfo.builder().build())
                .userBasic(UserBasic.builder()
                        .address("동대문구")
                        .gender(Gender.MALE)
                        .birth(LocalDate.of(1994, 3, 30))
                        .build())
                .build();
    }

    @DisplayName("단일 유저 조회")
    @Test
    void findUserByUserIdAndSnsType() {
        //  given
        given(userRepository.findByUserIdAndSnsType(user.getUserId(), user.getSnsType()))
                .willReturn(Optional.of(User.builder()
                        .userId(user.getUserId())
                        .build()));

        //  when
        UserDTO userDTO = userFindService.findUserByUserIdAndSnsType(user.getUserId(), user.getSnsType().getValue());

        //  then
        assertThat(userDTO.getUserId()).isEqualTo(user.getUserId());
    }

//    @DisplayName("유저조회(usrNo)")
//    @Test
//    void findUserByUsrNo() {
//        //  given
//        given(userRepository.findById(1L)).willReturn(Optional.of(User.builder()
//                .email(user.getEmail())
//                .build()));
//
//        //  when
//        UserDTO userDTO = userFindService.findUserByUsrNo(1L);
//
//        //  then
//        assertThat(userDTO.getEmail()).isEqualTo(user.getEmail());
//    }

//    @DisplayName("유저조회(email)")
//    @Test
//    void findUserByEmail() {
//        //  given
//        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
//
//        //  when
//        UserDTO userDTO = userFindService.findUserByEmail(user.getEmail());
//
//        //  then
//        assertThat(userDTO.getEmail()).isEqualTo(user.getEmail());
//    }

//    @DisplayName("유저조회(email) 실패 - UserNotFound")
//    @Test
//    void findUserByIdFailUserNotFound() {
//        //  given
//        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.empty());
//
//        //  when
//        ErrorCode error = assertThrows(UserException.class,
//                () -> userFindService.findUserByEmail(user.getEmail())).getErrorCode();
//
//        //  then
//        assertThat(error).isEqualTo(ErrorCode.USER_NOT_FOUND);
//    }

//    @DisplayName("유저 목록 조회")
//    @Test
//    void findUsers() {
//        //  given
//        given(userRepository.findAll()).willReturn(Arrays.asList(user, user2));
//
//        //  when
//        List<User> users = userFindService.findUsers();
//
//        //  then
//        assertThat(users.size()).isEqualTo(2);
//    }

    @DisplayName("유저 목록 조회 - page")
    @Test
    void findUsersPage() {
        //  given
        given(userRepository.findAll(PageRequest.of(0, 2))).willReturn(new PageImpl<>(List.of(user, user2)));

        //  when
        List<User> users = userFindService.findUsers(0, 2);

        //  then
        assertThat(users.size()).isEqualTo(2);
    }

    @DisplayName("유저 목록 조회 실패 - 유저 없음")
    @Test
    void findUserPageFailNotUserFound() {
        //  given
        given(userRepository.findAll(PageRequest.of(0, 2))).willReturn(new PageImpl<>(List.of()));

        //  when
        ErrorCode error = assertThrows(UserException.class,
                () -> userFindService.findUsers(0, 2)).getErrorCode();

        //  then
        assertThat(error).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }
}