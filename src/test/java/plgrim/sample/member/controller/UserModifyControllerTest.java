package plgrim.sample.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
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
import plgrim.sample.common.enums.SuccessCode;
import plgrim.sample.member.controller.dto.user.UserDTO;
import plgrim.sample.member.controller.dto.user.UserModifyDTO;
import plgrim.sample.member.domain.model.aggregates.User;
import plgrim.sample.member.domain.model.valueobjects.UserBasic;
import plgrim.sample.member.infrastructure.repository.UserJPARepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@DisplayName("UserModifyController 테스트")
@AutoConfigureMockMvc
class UserModifyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    UserJPARepository userRepository;

    // 테스트 데이터
    User user = User.builder()
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

    UserModifyDTO userModifyDTO;

    @Test
    @DisplayName("유저 수정 성공")
    void modifyUser() throws Exception {
        //  given
        userRepository.save(user);
        userModifyDTO = UserModifyDTO.builder()
                .usrNo(user.getUsrNo())
                .email("monty@plgrim.com")
                .password("123123213")
                .phoneNumber("01080140922")
                .address("dongtan")
                .gender(Gender.MALE)
                .birth(LocalDate.of(2021, 9, 9))
                .snsType(Sns.GOOGLE)
                .build();
        String content = objectMapper.writeValueAsString(userModifyDTO);

        //  when
        String resultAsString = mockMvc.perform(put("/api/user")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDTO result = objectMapper.readValue(resultAsString, UserDTO.class);

        System.out.println("result = " + result);

        //  then
//        org.assertj.core.api.Assertions.assertThat(result).usingRecursiveComparison()
//                .ignoringFields("usrNo")

    }

    @Test
    @DisplayName("유저 삭제 성공")
    void deleteUser() throws Exception {
        //  given
        userRepository.save(user);

        //  when
        mockMvc.perform(delete("/api/user")
                        .queryParam("usrNo", Long.toString(user.getUsrNo())))
                .andExpect(status().isOk())
                .andExpect(content().string(SuccessCode.DELETE_SUCCESS.getDetail()))
                .andDo(print());

        //  then
        assertTrue(userRepository.findById(user.getUsrNo()).isEmpty());
    }
}