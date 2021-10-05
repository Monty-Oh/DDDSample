package plgrim.sample.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
    @DisplayName("회원 조회")
    void findUserById() throws Exception {
        //  given
        String content = objectMapper.writeValueAsString(userJoinDTO);
        mockMvc.perform(post("/API/user/join")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        //  when, then
        mockMvc.perform(get("/API/user/{email}", userJoinDTO.getEmail()))
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
    @DisplayName("회원 조회 실패 - 없는 회원")
    void findUserByIdFailNotUserFound() throws Exception {
        // given
        // when, then
        mockMvc.perform(get("/API/user/{id}", userJoinDTO.getEmail()))
                .andExpect(status().isNotFound())
                .andExpect(content().string(ErrorCode.MEMBER_NOT_FOUND.getDetail()))
                .andDo(print());
    }

    @Test
    @DisplayName("회원 조회 실패 - (Validation) 이메일 형식")
    void findUserByIdFailEmailValidation() throws Exception {
        //  given
        String email = "monty@plgrim";

        //  when    //  then
        mockMvc.perform(get("/API/user/{id}", email))
                .andExpect(status().isBadRequest());
    }
}