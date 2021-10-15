package plgrim.sample.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import plgrim.sample.common.enums.ErrorCode;
import plgrim.sample.common.enums.Gender;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.member.controller.dto.user.UserDTO;
import plgrim.sample.member.domain.model.aggregates.User;
import plgrim.sample.member.domain.model.valueobjects.UserBasic;
import plgrim.sample.member.infrastructure.repository.UserJPARepository;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@DisplayName("UserFindController 테스트")
@AutoConfigureMockMvc
class UserFindControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserJPARepository userRepository;

    User user;
    User user2;
    User user3;

    @BeforeEach
    void setupDto() {
        user = User.builder()
                .email("monty@plgrim.com")
                .password("12345")
                .phoneNumber("01040684490")
                .userBasic(UserBasic.builder()
                        .address("dondaemungu")
                        .gender(Gender.MALE)
                        .snsType(Sns.LOCAL)
                        .birth(LocalDate.of(1994, 3, 30))
                        .build())
                .build();

        user2 = User.builder()
                .email("monty2@plgrim.com")
                .password("123456")
                .phoneNumber("01040684491")
                .userBasic(UserBasic.builder()
                        .address("dongtan")
                        .gender(Gender.MALE)
                        .birth(LocalDate.of(1994, 3, 30))
                        .snsType(Sns.LOCAL)
                        .build())
                .build();
        user3 = User.builder()
                .email("monty3@plgrim.com")
                .password("123456")
                .phoneNumber("01040684492")
                .userBasic(UserBasic.builder()
                        .address("seongsu")
                        .gender(Gender.MALE)
                        .birth(LocalDate.of(1994, 3, 30))
                        .snsType(Sns.LOCAL)
                        .build())
                .build();
    }

    @DisplayName("회원조회 usrNo")
    @Test
    void findUserByUsrNo() throws Exception {
        //  given
        userRepository.save(user);

        //  when
        MvcResult mvcResult = mockMvc.perform(get("/users/{usrNo}", Long.toString(user.getUsrNo())))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        UserDTO result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDTO.class);

        //  then
        //        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(result).usingRecursiveComparison().ignoringFields("password").isEqualTo(user);
    }

    @Test
    @DisplayName("회원조회 usrNo 실패 - 없는 회원")
    void findUserByEmail() throws Exception {
        //  given

        //  when
        mockMvc.perform(get("/users/{usrNo}", Long.toString(1L)))
                //  then
                .andExpect(status().isNotFound())
                .andExpect(content().string(ErrorCode.MEMBER_NOT_FOUND.getDetail()))
                .andDo(print());
    }

    @Test
    @DisplayName("회원 목록 조회")
    void findUserList() throws Exception {
        // given
        userRepository.saveAll(List.of(user, user2, user3));
        MultiValueMap<String, String> query = new LinkedMultiValueMap<>(){{
            add("page", Integer.toString(0));
            add("size", Integer.toString(2));
        }};

        //  when
        MvcResult mvcResult = mockMvc.perform(get("/users").queryParams(query))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        List<User> users = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, User.class));

        //  then
        assertThat(users.size()).isEqualTo(2);
    }
}