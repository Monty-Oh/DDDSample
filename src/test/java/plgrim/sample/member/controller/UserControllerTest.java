package plgrim.sample.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
@DisplayName("UserController 테스트")
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 테스트 데이터
    UserJoinDTO userJoinDTO = UserJoinDTO.builder()
            .id("monty@plgrim.com")
            .password("12345")
            .phoneNumber("01040684490")
            .address("동대문구")
            .gender(Gender.MALE)
            .birth(LocalDate.of(1994, 3, 30))
            .SnsType(Sns.LOCAL)
            .build();

    @BeforeEach
    void setup() {

    }

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
                                .id("monty@plgrim.commm")
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
        mockMvc.perform(get("/API/user").param("id", userJoinDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        UserDTO.builder()
                                .id(userJoinDTO.getId())
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
        mockMvc.perform(get("/API/user").param("id", userJoinDTO.getId()))
                .andExpect(status().isNotFound())
                .andExpect(content().string(ErrorCode.MEMBER_NOT_FOUND.getDetail()))
                .andDo(print());
    }
}