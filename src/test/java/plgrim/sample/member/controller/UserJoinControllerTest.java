package plgrim.sample.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import plgrim.sample.common.enums.ErrorCode;
import plgrim.sample.common.enums.Gender;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.member.application.UserJoinService;
import plgrim.sample.member.controller.dto.mapper.UserCommandMapper;
import plgrim.sample.member.controller.dto.user.UserJoinDTO;
import plgrim.sample.member.domain.model.commands.UserJoinCommand;
import plgrim.sample.member.domain.model.valueobjects.UserBasic;

import java.time.LocalDate;

import static org.apache.logging.log4j.util.Strings.isNotBlank;
import static org.mockito.BDDMockito.given;
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
        mockMvc.perform(post("/API/user")
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

        mockMvc.perform(post("/API/user")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(post("/API/user")
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

        mockMvc.perform(post("/API/user")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(post("/API/user")
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

    /**
     * 회원가입
     * email validation 반복 테스트
     */
    @DisplayName("회원가입 실패 - (Validation)")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"12312aㅁㄴㅇㅁㄴ23"})
    void joinFailEmailValidation(String email) throws Exception {
        String content = objectMapper.writeValueAsString(UserJoinDTO.builder()
                .email(email)
                .password("12345")
                .phoneNumber("01040684490")
                .address("동대문구")
                .gender(Gender.MALE)
                .birth(LocalDate.of(1994, 3, 30))
                .SnsType(Sns.LOCAL)
                .build());

        mockMvc.perform(post("/API/user")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(isNotBlank(email)
                        ? ErrorCode.VALIDATION_ERROR_ID.getDetail()
                        : ErrorCode.VALIDATION_ERROR_ID_EMPTY.getDetail()))
                .andDo(print());
    }

    /**
     * 회원가입
     * email validation 반복 테스트
     */
    @DisplayName("회원가입 실패 - (Validation)")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"0", "000000000000000000000000000000000000000000000000000000000"})
    void joinFailPasswordValidation(String password) throws Exception {
        String content = objectMapper.writeValueAsString(UserJoinDTO.builder()
                .email("monty@plgrim.com")
                .password(password)
                .phoneNumber("01040684490")
                .address("동대문구")
                .gender(Gender.MALE)
                .birth(LocalDate.of(1994, 3, 30))
                .SnsType(Sns.LOCAL)
                .build());

        mockMvc.perform(post("/API/user")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(isNotBlank(password)
                        ? ErrorCode.VALIDATION_ERROR_PASSWORD.getDetail()
                        : ErrorCode.VALIDATION_ERROR_PASSWORD_EMPTY.getDetail()))
                .andDo(print());
    }
}