package plgrim.sample.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.mock.web.MockHttpServletResponse;
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
import plgrim.sample.common.token.TokenProviderFactory;
import plgrim.sample.member.application.UserFindService;
import plgrim.sample.member.application.UserJoinService;
import plgrim.sample.member.application.UserModifyService;
import plgrim.sample.member.controller.dto.mapper.UserCommandMapper;
import plgrim.sample.member.controller.dto.user.UserDTO;
import plgrim.sample.member.domain.model.aggregates.User;
import plgrim.sample.member.domain.model.entities.UserRole;
import plgrim.sample.member.domain.model.valueobjects.SnsInfo;
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
import static plgrim.sample.common.UrlValue.USER_ID;

@DisplayName("UserFindController 테스트")
@WebMvcTest
@WithMockUser(roles = "USER")
@MockBeans({
        @MockBean(UserFindService.class),
        @MockBean(UserJoinService.class),
        @MockBean(UserModifyService.class),
        @MockBean(UserCommandMapper.class),
        @MockBean(TokenProviderFactory.class),
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
                .userId("monty")
                .email("monty@plgrim.com")
                .password("123456")
                .nickName("monty")
                .mobileNo("01040684490")
                .snsType(Sns.LOCAL)
                .roles(List.of(UserRole.builder().authority("ROLE_USER").build()))
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
                .roles(List.of(UserRole.builder().authority("ROLE_USER").build()))
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
                .roles(List.of(UserRole.builder().authority("ROLE_USER").build()))
                .snsInfo(SnsInfo.builder().build())
                .userBasic(UserBasic.builder()
                        .address("동대문구")
                        .gender(Gender.MALE)
                        .birth(LocalDate.of(1994, 3, 30))
                        .build())
                .build();

        userDTO = UserDTO.builder()
                .usrNo(1L)
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickName(user.getNickName())
                .mobileNo(user.getMobileNo())
                .snsType(user.getSnsType())
                .snsInfo(user.getSnsInfo())
                .userBasic(user.getUserBasic())
                .build();
    }

    @DisplayName("회원조회 userId")
    @Test
    void findUserByUsrNo() throws Exception {
        //  given
        given(userFindService.findUserByUserIdAndSnsType(user.getUserId(), user.getSnsType().getValue()))
                .willReturn(userDTO);

        //  when
        MvcResult mvcResult = mockMvc.perform(get(ROOT_USER_PATH + USER_ID, user.getUserId())
                        .queryParam("snsType", user.getSnsType().getValue()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        UserDTO result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDTO.class);

        //  then
        assertThat(result).usingRecursiveComparison().ignoringFields("password").isEqualTo(user);
    }

    @Test
    @DisplayName("회원조회 userId 실패 - 없는 회원")
    void findUserByEmail() throws Exception {
        //  given
        given(userFindService.findUserByUserIdAndSnsType(user.getUserId(), user.getSnsType().getValue()))
                .willThrow(new UserException(ErrorCode.USER_NOT_FOUND));

        //  when
        MockHttpServletResponse mvcResult = mockMvc.perform(get(ROOT_USER_PATH + USER_ID, user.getUserId())
                        .queryParam("snsType", user.getSnsType().getValue()))
                //  then
                .andExpect(status().isNotFound())
                .andExpect(content().string(ErrorCode.USER_NOT_FOUND.getDetail()))
                .andDo(print())
                .andReturn()
                .getResponse();

        //  then
        assertThat(mvcResult.getStatus()).isEqualTo(ErrorCode.USER_NOT_FOUND.getHttpStatus().value());
        assertThat(mvcResult.getContentAsString()).isEqualTo(ErrorCode.USER_NOT_FOUND.getDetail());
    }

    @Test
    @DisplayName("회원 목록 조회")
    void findUserList() throws Exception {
        // given
        List<User> getList = List.of(user, user2);
        given(userFindService.findUsers(0, 2)).willReturn(getList);
        MultiValueMap<String, String> query = new LinkedMultiValueMap<>() {{
            add("page", Integer.toString(0));
            add("size", Integer.toString(2));
        }};
        String expected = objectMapper.writeValueAsString(getList);

        //  when
        mockMvc.perform(get(ROOT_USER_PATH).queryParams(query))
                .andExpect(status().isOk())
                .andExpect(content().string(expected))
                .andDo(print())
                .andReturn();
    }
}