package plgrim.sample.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import plgrim.sample.common.enums.Gender;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.member.controller.dto.user.UserJoinDTO;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@DisplayName("UserController 테스트")
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    UserJoinDTO userJoinDTO = UserJoinDTO.builder()
            .id("monty@plgrim.com")
            .password("12345")
            .phoneNumber("01040684490")
            .address("동대문구")
            .gender(Gender.MALE)
            .birth(LocalDate.of(1994, 3, 30))
            .SnsType(Sns.LOCAL)
            .build();                                                   // 테스트 데이터

    @Test
    @DisplayName("회원가입 호출")
    void join() throws Exception {

        String content = objectMapper.writeValueAsString(userJoinDTO);  // JSON data 생성

        mockMvc.perform(post("/API/user/join")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("monty@plgrim.com"))
                .andDo(print());                                        // 가입 요청 호출
    }

    @Test
    @DisplayName("회원가입 실패 - ID 중복")
    void join_fail_duplicated_id() throws Exception {
        String content = objectMapper.writeValueAsString(userJoinDTO);

        mockMvc.perform(post("/API/user/join")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(post("/API/user/join")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().string("이미 중복된 회원이 존재합니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패 - 번호 중복")
    void join_fail_duplicated_phoneNum() throws Exception {

    }
}