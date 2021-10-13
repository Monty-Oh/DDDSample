package plgrim.sample.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import plgrim.sample.common.enums.Gender;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.member.controller.dto.user.UserModifyDTO;
import plgrim.sample.member.domain.model.aggregates.User;
import plgrim.sample.member.domain.model.valueobjects.UserBasic;
import plgrim.sample.member.infrastructure.repository.UserJPARepository;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
                    .address("동대문구")
                    .gender(Gender.MALE)
                    .birth(LocalDate.of(1994, 3, 30))
                    .snsType(Sns.LOCAL)
                    .build())
            .build();

    UserModifyDTO userModifyDTO = UserModifyDTO.builder()
            .email("monty@plgrim.com")
            .password("123123213")
            .phoneNumber("01080140922")
            .address("동탄")
            .gender(Gender.MALE)
            .birth(LocalDate.of(2021, 9, 9))
            .snsType(Sns.GOOGLE)
            .build();

    @Test
    @DisplayName("유저 수정")
    void modify() throws Exception {
        //  given
        userRepository.save(user);
        System.out.println(user);
        String content = objectMapper.writeValueAsString(userModifyDTO);

        //  when    //  then
        mockMvc.perform(put("/API/user")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}