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
import plgrim.sample.member.domain.model.aggregates.User;
import plgrim.sample.member.domain.model.valueobjects.UserBasic;
import plgrim.sample.member.domain.service.UserRepository;
import plgrim.sample.member.infrastructure.repository.UserJPARepository;

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

    @Autowired
    private UserJPARepository userRepository;

    private User user;

    @BeforeEach
    void setupDto() {
        user = User.builder()
                .email("monty@plgrim.com")
                .password("12345")
                .phoneNumber("01040684490")
                .userBasic(UserBasic.builder()
                        .address("동대문구")
                        .gender(Gender.MALE)
                        .snsType(Sns.LOCAL)
                        .birth(LocalDate.of(1994, 3, 30))
                        .build())
                .build();
    }

    @DisplayName("회원조회 usrNo")
    @Test
    void findUserByUsrNo() throws Exception {
        //  given
        userRepository.save(user);

        //  when
        mockMvc.perform(get("/api/user").queryParam("usrNo", Long.toString(user.getUsrNo())))
                //  then
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        UserDTO.builder()
                                .usrNo(user.getUsrNo())
                                .email(user.getEmail())
                                .phoneNumber(user.getPhoneNumber())
                                .userBasic(user.getUserBasic())
                                .build())))
                .andDo(print());
    }

    @Test
    @DisplayName("회원조회 usrNo 실패 - 없는 회원")
    void findUserByEmail() throws Exception {
        //  given

        //  when
        mockMvc.perform(get("/api/user").queryParam("usrNo", Long.toString(1L)))
                //  then
                .andExpect(status().isNotFound())
                .andExpect(content().string(ErrorCode.MEMBER_NOT_FOUND.getDetail()))
                .andDo(print());
    }
}