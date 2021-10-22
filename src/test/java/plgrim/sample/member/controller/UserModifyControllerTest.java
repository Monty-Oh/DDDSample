package plgrim.sample.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import plgrim.sample.common.KakaoTokenProvider;
import plgrim.sample.common.LocalTokenProvider;
import plgrim.sample.common.enums.Gender;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.common.enums.SuccessCode;
import plgrim.sample.member.application.UserFindService;
import plgrim.sample.member.application.UserJoinService;
import plgrim.sample.member.application.UserModifyService;
import plgrim.sample.member.controller.dto.mapper.UserCommandMapper;
import plgrim.sample.member.controller.dto.user.UserDTO;
import plgrim.sample.member.controller.dto.user.UserModifyDTO;
import plgrim.sample.member.domain.model.aggregates.User;
import plgrim.sample.member.domain.model.valueobjects.UserBasic;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static plgrim.sample.common.UrlValue.USRNO_PATH;
import static plgrim.sample.common.UrlValue.ROOT_USER_PATH;

@DisplayName("UserModifyController 테스트")
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
class UserModifyControllerTest {
    @MockBean
    UserModifyService userModifyService;
    @MockBean
    UserCommandMapper userCommandMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 테스트 데이터
    User user;
    UserDTO userDTO;
    UserModifyDTO userModifyDTO;

    @BeforeEach
    void setup() {
        user = User.builder()
                .usrNo(1L)
                .email("monty@plgrim.com")
                .password("test")
                .phoneNumber("01040684490")
                .userBasic(UserBasic.builder()
                        .address("domdaemungu")
                        .gender(Gender.MALE)
                        .birth(LocalDate.of(1994, 3, 30))
                        .snsType(Sns.LOCAL)
                        .build())
                .build();

        userModifyDTO = UserModifyDTO.builder()
                .usrNo(user.getUsrNo())
                .email(user.getEmail())
                .password(user.getPassword())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getUserBasic().getAddress())
                .gender(user.getUserBasic().getGender())
                .birth(user.getUserBasic().getBirth())
                .snsType(user.getUserBasic().getSnsType())
                .build();

        userDTO = UserDTO.builder()
                .usrNo(user.getUsrNo())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .userBasic(user.getUserBasic())
                .build();
    }

    @Test
    @DisplayName("유저 수정 성공")
    void modifyUser() throws Exception {
        //  given
        String content = objectMapper.writeValueAsString(userModifyDTO);
        given(userModifyService.modify(any())).willReturn(userDTO);

        //  when
        String resultAsString = mockMvc.perform(put(ROOT_USER_PATH + USRNO_PATH, user.getUsrNo())
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();
        UserDTO result = objectMapper.readValue(resultAsString, UserDTO.class);

        //  then
        assertThat(result).usingRecursiveComparison()
                .ignoringFields("password")
                .isEqualTo(user);
    }

    @Test
    @DisplayName("유저 삭제 성공")
    void deleteUser() throws Exception {
        //  given
        doNothing().when(userModifyService).delete(user.getUsrNo());

        //  when
        mockMvc.perform(delete(ROOT_USER_PATH + USRNO_PATH, user.getUsrNo()))
                .andExpect(status().isOk())
                .andExpect(content().string(SuccessCode.DELETE_SUCCESS.getDetail()))
                .andDo(print());

        //  then
    }
}