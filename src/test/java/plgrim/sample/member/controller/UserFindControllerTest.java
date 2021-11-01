package plgrim.sample.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import plgrim.sample.common.enums.ErrorCode;
import plgrim.sample.common.enums.Gender;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.common.exceptions.UserException;
import plgrim.sample.common.token.KakaoTokenProvider;
import plgrim.sample.common.token.LocalTokenProvider;
import plgrim.sample.member.application.UserFindService;
import plgrim.sample.member.application.UserJoinService;
import plgrim.sample.member.application.UserModifyService;
import plgrim.sample.member.controller.dto.mapper.UserCommandMapper;
import plgrim.sample.member.controller.dto.user.UserDTO;
import plgrim.sample.member.domain.model.aggregates.User;
import plgrim.sample.member.domain.model.valueobjects.UserBasic;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static plgrim.sample.common.UrlValue.ROOT_USER_PATH;
import static plgrim.sample.common.UrlValue.USRNO_PATH;

@DisplayName("UserFindController 테스트")
@WebMvcTest
@WithMockUser(roles = "USER")
@MockBeans({
        @MockBean(UserFindService.class),
        @MockBean(UserJoinService.class),
        @MockBean(UserModifyService.class),
        @MockBean(UserCommandMapper.class),
        @MockBean(LocalTokenProvider.class),
        @MockBean(KakaoTokenProvider.class),
        @MockBean(LoginController.class),
        @MockBean(UserDetailsService.class)
})
class UserFindControllerTest {
    @MockBean
    UserFindService userFindService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    // 테스트 데이터
    User user;
    User user2;
    User user3;
    UserDTO userDTO;

    @BeforeEach
    void setupDto() {
        user = User.builder()
                .usrNo(1L)
                .email("monty@plgrim.com")
                .password("12345")
                .phoneNumber("01040684490")
                .userBasic(UserBasic.builder()
                        .address("dondaemungu")
                        .gender(Gender.MALE)
                        .snsType(Sns.LOCAL)
                        .birth(LocalDate.of(1994, 3, 30))
                        .build())
                .build();

        user2 = User.builder()
                .usrNo(2L)
                .email("monty2@plgrim.com")
                .password("123456")
                .phoneNumber("01040684491")
                .userBasic(UserBasic.builder()
                        .address("dongtan")
                        .gender(Gender.MALE)
                        .birth(LocalDate.of(1994, 3, 30))
                        .snsType(Sns.LOCAL)
                        .build())
                .build();
        user3 = User.builder()
                .usrNo(3L)
                .email("monty3@plgrim.com")
                .password("123456")
                .phoneNumber("01040684492")
                .userBasic(UserBasic.builder()
                        .address("seongsu")
                        .gender(Gender.MALE)
                        .birth(LocalDate.of(1994, 3, 30))
                        .snsType(Sns.LOCAL)
                        .build())
                .build();

        userDTO = UserDTO.builder()
                .usrNo(1L)
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .userBasic(user.getUserBasic())
                .build();
    }

    @DisplayName("회원조회 usrNo")
    @Test
//    @WithMockUser(roles = "USER")
    void findUserByUsrNo() throws Exception {
        //  given
        given(userFindService.findUserByUsrNo(user.getUsrNo())).willReturn(userDTO);

        //  when
        MvcResult mvcResult = mockMvc.perform(get(ROOT_USER_PATH + USRNO_PATH, Long.toString(user.getUsrNo())))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        UserDTO result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDTO.class);

        //  then
        assertThat(result).usingRecursiveComparison().ignoringFields("password").isEqualTo(user);
    }

    @Test
    @DisplayName("회원조회 usrNo 실패 - 없는 회원")
//    @WithMockUser(roles = "USER")
    void findUserByEmail() throws Exception {
        //  given
        given(userFindService.findUserByUsrNo(user.getUsrNo())).willThrow(new UserException(ErrorCode.USER_NOT_FOUND));

        //  when
        mockMvc.perform(get(ROOT_USER_PATH + USRNO_PATH, Long.toString(1L)))
                //  then
                .andExpect(status().isNotFound())
                .andExpect(content().string(ErrorCode.USER_NOT_FOUND.getDetail()))
                .andDo(print());
    }

    @Test
    @DisplayName("회원 목록 조회")
//    @WithMockUser(roles = "USER")
    void findUserList() throws Exception {
        // given
        List<User> getList = List.of(user, user2);
        given(userFindService.findUsers(0, 2)).willReturn(getList);
        MultiValueMap<String, String> query = new LinkedMultiValueMap<>() {{
            add("page", Integer.toString(0));
            add("size", Integer.toString(2));
        }};

        //  when
        MvcResult mvcResult = mockMvc.perform(get(ROOT_USER_PATH).queryParams(query))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        List<User> users = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, User.class));

        //  then
        assertThat(users.size()).isEqualTo(2);
    }
}