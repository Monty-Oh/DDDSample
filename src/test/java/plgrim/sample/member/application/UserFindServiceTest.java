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
import plgrim.sample.member.controller.dto.user.UserFindByIdDTO;
import plgrim.sample.member.domain.model.aggregates.User;
import plgrim.sample.member.domain.model.valueobjects.UserBasic;
import plgrim.sample.member.infrastructure.repository.UserJPARepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@DisplayName("UserFindService 테스트")
@ExtendWith(MockitoExtension.class)
class UserFindServiceTest {
    @Mock
    UserJPARepository userRepository;

    @InjectMocks
    UserFindService userFindService;

    User user;
    User user2;
    User user3;

    UserFindByIdDTO userFindByIdDTO;

    @BeforeEach
    void setup() {
        user = User.builder()
                .email("monty@plgrim.com")
                .password("123456")
                .phoneNumber("01040684490")
                .userBasic(UserBasic.builder()
                        .address("동대문구")
                        .gender(Gender.MALE)
                        .birth(LocalDate.of(1994, 3, 30))
                        .snsType(Sns.LOCAL)
                        .build())
                .build();
        user2 = User.builder()
                .email("monty@plgrim.com")
                .password("123456")
                .phoneNumber("01040684490")
                .userBasic(UserBasic.builder()
                        .address("동대문구")
                        .gender(Gender.MALE)
                        .birth(LocalDate.of(1994, 3, 30))
                        .snsType(Sns.LOCAL)
                        .build())
                .build();
        user3 = User.builder()
                .email("monty@plgrim.com")
                .password("123456")
                .phoneNumber("01040684490")
                .userBasic(UserBasic.builder()
                        .address("동대문구")
                        .gender(Gender.MALE)
                        .birth(LocalDate.of(1994, 3, 30))
                        .snsType(Sns.LOCAL)
                        .build())
                .build();

        userFindByIdDTO = UserFindByIdDTO.builder()
                .email(user.getEmail())
                .build();
    }

    @DisplayName("유저조회(usrNo)")
    @Test
    void findUserByUsrNo() {
        //  given
        given(userRepository.findById(1L)).willReturn(Optional.of(User.builder()
                .usrNo(1L)
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .userBasic(user.getUserBasic())
                .build()));

        //  when
        UserDTO userDTO = userFindService.findUserByUsrNo(1L);

        //  then
        assertThat(userDTO.getEmail()).isEqualTo(user.getEmail());
    }

    @DisplayName("유저조회(email)")
    @Test
    void findUserByEmail() {
        //  given
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

        //  when
        UserDTO userDTO = userFindService.findUserByEmail(userFindByIdDTO.getEmail());

        //  then
        assertThat(userDTO.getEmail()).isEqualTo(user.getEmail());
    }

    @DisplayName("유저조회(email) 실패 - UserNotFound")
    @Test
    void findUserByIdFailUserNotFound() {
        //  given
        given(userRepository.findByEmail(user.getEmail()))
                .willReturn(Optional.empty())
                .willThrow(new UserException(ErrorCode.MEMBER_NOT_FOUND));

        //  when    //  then
        assertThrows(UserException.class,
                () -> userFindService.findUserByEmail(userFindByIdDTO.getEmail()));
    }

    @DisplayName("유저 목록 조회")
    @Test
    void findUsers() {
        //  given
        given(userRepository.findAll()).willReturn(Arrays.asList(user, user2));

        //  when
        List<User> users = userFindService.findUsers();

        //  then
        assertThat(users.size()).isEqualTo(2);
    }

    @DisplayName("유저 목록 조회 - page")
    @Test
    void findUsersPage() {
        //  given
        given(userRepository.findAll(PageRequest.of(0, 2)))
                .willReturn(new PageImpl<>(Arrays.asList(user, user2)));

        //  when
        List<User> users = userFindService.findUsers(0, 2);

        //  then
        assertThat(users.size()).isEqualTo(2);
    }
}