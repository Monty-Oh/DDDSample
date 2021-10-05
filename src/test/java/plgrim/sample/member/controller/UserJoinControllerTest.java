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
import plgrim.sample.common.enums.ErrorCode;
import plgrim.sample.common.enums.Gender;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.member.controller.dto.user.UserJoinDTO;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@DisplayName("UserJoinController 테스트")
@AutoConfigureMockMvc
class UserJoinControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 테스트 데이터
    UserJoinDTO userJoinDTO = UserJoinDTO.builder()
            .email("monty@plgrim.com")
            .password("12345")
            .phoneNumber("01040684490")
            .address("동대문구")
            .gender(Gender.MALE)
            .birth(LocalDate.of(1994, 3, 30))
            .SnsType(Sns.LOCAL)
            .build();

    @Test
    @DisplayName("회원가입 호출")
    void join() throws Exception {
        //  given
        String content = objectMapper.writeValueAsString(userJoinDTO);  // JSON data 생성

        //  when, then
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
                .andExpect(content().string(ErrorCode.DUPLICATE_ID.getDetail()))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패 - 번호 중복")
    void join_fail_duplicated_phoneNum() throws Exception {
        String content = objectMapper.writeValueAsString(userJoinDTO);

        mockMvc.perform(post("/API/user/join")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(post("/API/user/join")
                        .content(objectMapper.writeValueAsString(UserJoinDTO.builder()
                                .email("monty@plgrim.commm")
                                .password("12345")
                                .phoneNumber("01040684490")
                                .address("동대문구")
                                .gender(Gender.MALE)
                                .birth(LocalDate.of(1994, 3, 30))
                                .SnsType(Sns.LOCAL)
                                .build()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().string(ErrorCode.DUPLICATE_PHONE_NUMBER.getDetail()))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패 - (Validation) ID 공백")
    void join_fail_validated_id_empty() throws Exception {
        String content = objectMapper.writeValueAsString(UserJoinDTO.builder()
                .email("")
                .password("12345")
                .phoneNumber("01040684490")
                .address("동대문구")
                .gender(Gender.MALE)
                .birth(LocalDate.of(1994, 3, 30))
                .SnsType(Sns.LOCAL)
                .build());

        // 공백으로 인한 에러
        mockMvc.perform(post("/API/user/join")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(ErrorCode.VALIDATION_ERROR_ID_EMPTY.getDetail()))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패 - (Validation) 형식")
    void join_fail_validated_id() throws Exception {
        String content = objectMapper.writeValueAsString(UserJoinDTO.builder()
                .email("121ㄱㅇㄴㅇㅍ3212")
                .password("12345")
                .phoneNumber("01040684490")
                .address("동대문구")
                .gender(Gender.MALE)
                .birth(LocalDate.of(1994, 3, 30))
                .SnsType(Sns.LOCAL)
                .build());

        // 이메일 형식으로 인한 에러
        mockMvc.perform(post("/API/user/join")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(ErrorCode.VALIDATION_ERROR_ID.getDetail()))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패 - (Validation) 패스워드 공백")
    void join_fail_validated_password_empty() throws Exception {
        String content = objectMapper.writeValueAsString(UserJoinDTO.builder()
                .email("monty@plgrim.com")
                .password("")
                .phoneNumber("01040684490")
                .address("동대문구")
                .gender(Gender.MALE)
                .birth(LocalDate.of(1994, 3, 30))
                .SnsType(Sns.LOCAL)
                .build());
        // 글자수 부족으로 인한 에러
        mockMvc.perform(post("/API/user/join")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(ErrorCode.VALIDATION_ERROR_PASSWORD_EMPTY.getDetail()))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패 - (Validation) 패스워드 글자수 부족")
    void join_fail_validated_password() throws Exception {
        String content = objectMapper.writeValueAsString(UserJoinDTO.builder()
                .email("monty@plgrim.com")
                .password("0")
                .phoneNumber("01040684490")
                .address("동대문구")
                .gender(Gender.MALE)
                .birth(LocalDate.of(1994, 3, 30))
                .SnsType(Sns.LOCAL)
                .build());
        // 글자수 부족으로 인한 에러
        mockMvc.perform(post("/API/user/join")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(ErrorCode.VALIDATION_ERROR_PASSWORD.getDetail()))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패 - (Validation) 패스워드 글자수 초과")
    void join_fail_validated_password2() throws Exception {
        //  given
        String content = objectMapper.writeValueAsString(UserJoinDTO.builder()
                .email("monty@plgrim.com")
                .password("000000000000000000000000000000000000000000000000000000000")
                .phoneNumber("01040684490")
                .address("동대문구")
                .gender(Gender.MALE)
                .birth(LocalDate.of(1994, 3, 30))
                .SnsType(Sns.LOCAL)
                .build());

        //  when, then
        mockMvc.perform(post("/API/user/join")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(ErrorCode.VALIDATION_ERROR_PASSWORD.getDetail()))
                .andDo(print());
    }
}