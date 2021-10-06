package plgrim.sample.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import plgrim.sample.common.enums.ErrorCode;
import plgrim.sample.common.enums.Gender;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.member.controller.dto.user.UserDTO;
import plgrim.sample.member.controller.dto.user.UserJoinDTO;
import plgrim.sample.member.domain.model.valueobjects.UserBasic;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@DisplayName("UserFindController 테스트")
@AutoConfigureMockMvc
class UserFindControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserJoinDTO userJoinDTO;

    @BeforeEach
    void setupDto() {
        userJoinDTO = UserJoinDTO.builder()
                .email("monty@plgrim.com")
                .password("12345")
                .phoneNumber("01040684490")
                .address("동대문구")
                .gender(Gender.MALE)
                .birth(LocalDate.of(1994, 3, 30))
                .SnsType(Sns.LOCAL)
                .build();
    }

    @Test
    @DisplayName("회원조회 email")
    void findUserByEmail() throws Exception {
        //  given
        String content = objectMapper.writeValueAsString(userJoinDTO);
        mockMvc.perform(post("/API/user")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        //  when, then
        mockMvc.perform(get("/API/user").queryParam("email", userJoinDTO.getEmail()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        UserDTO.builder()
                                .email(userJoinDTO.getEmail())
                                .phoneNumber(userJoinDTO.getPhoneNumber())
                                .userBasic(UserBasic.builder()
                                        .address(userJoinDTO.getAddress())
                                        .birth(userJoinDTO.getBirth())
                                        .gender(userJoinDTO.getGender())
                                        .snsType(userJoinDTO.getSnsType())
                                        .build())
                                .build())))
                .andDo(print());
    }

    @Test
    @DisplayName("회원조회 email 실패 - 없는 회원")
    void findUserByEmailFailNotUserFound() throws Exception {
        // given
        // when, then
        mockMvc.perform(get("/API/user").queryParam("email", userJoinDTO.getEmail()))
                .andExpect(status().isNotFound())
                .andExpect(content().string(ErrorCode.MEMBER_NOT_FOUND.getDetail()))
                .andDo(print());
    }

    //  테스트 반복
    @DisplayName("회원조회 email 실패 - (Validation)")
    @ParameterizedTest
    @NullAndEmptySource     //  null과 공백도 같이 넣어줌.
    @ValueSource(strings = {"monty@plgrim"})
    void findUserByIdFailEmailValidation(String email) throws Exception {
        //  when    //  then
        mockMvc.perform(get("/API/user").queryParam("email", email))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}