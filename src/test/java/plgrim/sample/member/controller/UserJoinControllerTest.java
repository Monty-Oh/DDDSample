package plgrim.sample.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import plgrim.sample.common.enums.ErrorCode;
import plgrim.sample.common.enums.Gender;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.common.exceptions.UserException;
import plgrim.sample.common.token.TokenProviderFactory;
import plgrim.sample.member.application.UserFindService;
import plgrim.sample.member.application.UserJoinService;
import plgrim.sample.member.application.UserModifyService;
import plgrim.sample.member.controller.dto.mapper.UserCommandMapper;
import plgrim.sample.member.controller.dto.user.UserDTO;
import plgrim.sample.member.controller.dto.user.UserJoinDTO;
import plgrim.sample.member.domain.model.aggregates.User;
import plgrim.sample.member.domain.model.valueobjects.SnsInfo;
import plgrim.sample.member.domain.model.valueobjects.UserBasic;

import java.time.LocalDate;

import static org.apache.logging.log4j.util.Strings.isNotBlank;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static plgrim.sample.common.UrlValue.ROOT_USER_PATH;

@DisplayName("UserJoinController 테스트")
@WebMvcTest
@WithMockUser(roles = "USER")
@MockBeans({
        @MockBean(UserFindService.class),
        @MockBean(UserJoinService.class),
        @MockBean(UserModifyService.class),
        @MockBean(UserCommandMapper.class),
        @MockBean(TokenProviderFactory.class),
        @MockBean(LoginController.class),
        @MockBean(UserDetailsService.class)
})
class UserJoinControllerTest {
    @MockBean
    UserJoinService userJoinService;
    @MockBean
    UserCommandMapper userCommandMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 테스트 데이터
    UserJoinDTO userJoinDTO;
    UserDTO userDTO;
    User user;

    @BeforeEach
    void setup() {
        user = User.builder()
                .userId("monty")
                .email("monty@plgrim.com")
                .password("12345")
                .nickName("monty")
                .mobileNo("01040684490")
                .snsType(Sns.LOCAL)
                .snsInfo(SnsInfo.builder().build())
                .userBasic(UserBasic.builder()
                        .address("dongdaemungu")
                        .gender(Gender.MALE)
                        .birth(LocalDate.of(1994, 3, 30))
                        .build())
                .build();

        userJoinDTO = UserJoinDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .password(user.getPassword())
                .nickName(user.getNickName())
                .mobileNo(user.getMobileNo())
                .snsType(user.getSnsType())
                .refreshToken(user.getSnsInfo().getRefreshToken())
                .address(user.getUserBasic().getAddress())
                .gender(user.getUserBasic().getGender())
                .birth(user.getUserBasic().getBirth())
                .build();

        userDTO = UserDTO.builder()
                .usrNo(1L)
                .build();
    }

    @Test
    @DisplayName("회원가입 호출")
    void join() throws Exception {
        //  given
        String content = objectMapper.writeValueAsString(userJoinDTO);  // JSON data 생성
        given(userJoinService.join(userCommandMapper.toCommand(userJoinDTO)))
                .willReturn(userDTO);

        //  when
        MvcResult mvcResult = mockMvc.perform(post(ROOT_USER_PATH)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        //  then
        UserDTO result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDTO.class);
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(userDTO);
    }

    @Test
    @DisplayName("회원가입 실패 - ID 중복")
    void join_fail_duplicated_id() throws Exception {
        //  given
        given(userJoinService.join(userCommandMapper.toCommand(userJoinDTO)))
                .willThrow(new UserException(ErrorCode.DUPLICATE_EMAIL));
        String content = objectMapper.writeValueAsString(userJoinDTO);

        //  when
        mockMvc.perform(post(ROOT_USER_PATH)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().string(ErrorCode.DUPLICATE_EMAIL.getDetail()))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패 - 번호 중복")
    void join_fail_duplicated_phoneNum() throws Exception {
        //  given
        given(userJoinService.join(userCommandMapper.toCommand(userJoinDTO)))
                .willThrow(new UserException(ErrorCode.DUPLICATE_MOBILE_NO));
        String content = objectMapper.writeValueAsString(userJoinDTO);

        //  when
        mockMvc.perform(post(ROOT_USER_PATH)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().string(ErrorCode.DUPLICATE_MOBILE_NO.getDetail()))
                .andDo(print());
    }

    /**
     * 회원가입
     * email validation 반복 테스트
     */
    @DisplayName("회원가입 실패 - (Email Validation)")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"12312aㅁㄴㅇㅁㄴ23"})
    void joinFailEmailValidation(String email) throws Exception {
        //  given
        String content = objectMapper.writeValueAsString(UserJoinDTO.builder()
                .userId(user.getUserId())
                .email(email)
                .password(user.getPassword())
                .nickName(user.getNickName())
                .mobileNo(user.getMobileNo())
                .snsType(user.getSnsType())
                .refreshToken(user.getSnsInfo().getRefreshToken())
                .address(user.getUserBasic().getAddress())
                .gender(user.getUserBasic().getGender())
                .birth(user.getUserBasic().getBirth())
                .build());

        mockMvc.perform(post(ROOT_USER_PATH)
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
     * password validation 반복 테스트
     */
    @DisplayName("회원가입 실패 - Password 형식")
    @ParameterizedTest
    @ValueSource(strings = {"0", "000000000000000000000000000000000000000000000000000000000"})
    void joinFailPasswordValidation(String password) throws Exception {
        //  given
        String content = objectMapper.writeValueAsString(UserJoinDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .password(password)
                .nickName(user.getNickName())
                .mobileNo(user.getMobileNo())
                .snsType(user.getSnsType())
                .refreshToken(user.getSnsInfo().getRefreshToken())
                .address(user.getUserBasic().getAddress())
                .gender(user.getUserBasic().getGender())
                .birth(user.getUserBasic().getBirth())
                .build());

        //  when
        MockHttpServletResponse response = mockMvc.perform(post("/users")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        //  then
        assertThat(response.getStatus()).isEqualTo(ErrorCode.VALIDATION_ERROR_PASSWORD.getHttpStatus().value());
        assertThat(response.getContentAsString()).isEqualTo(ErrorCode.VALIDATION_ERROR_PASSWORD.getDetail());
    }

    @DisplayName("회원가입 실패 - Password 형식")
    @ParameterizedTest
    @NullAndEmptySource
    void joinFailPasswordValidationEmptyValue(String password) throws Exception {
        //  given
        String content = objectMapper.writeValueAsString(UserJoinDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .password(password)
                .nickName(user.getNickName())
                .mobileNo(user.getMobileNo())
                .snsType(user.getSnsType())
                .refreshToken(user.getSnsInfo().getRefreshToken())
                .address(user.getUserBasic().getAddress())
                .gender(user.getUserBasic().getGender())
                .birth(user.getUserBasic().getBirth())
                .build());

        //  when
        MockHttpServletResponse response = mockMvc.perform(post("/users")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        //  then
        assertThat(response.getStatus()).isEqualTo(ErrorCode.VALIDATION_ERROR_PASSWORD_EMPTY.getHttpStatus().value());
        assertThat(response.getContentAsString()).isEqualTo(ErrorCode.VALIDATION_ERROR_PASSWORD_EMPTY.getDetail());
    }



    /**
     * 회원가입
     * PhoneNumber validation 반복 테스트
     */
    @DisplayName("회원가입 실패 - PhoneNumber 형식")
    @ParameterizedTest
    @ValueSource(strings = {"0", "01040684490000"})
    void joinFailPhoneNumberValidation(String mobileNo) throws  Exception {
        //  given
        String content = objectMapper.writeValueAsString(UserJoinDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .password(user.getPassword())
                .nickName(user.getNickName())
                .mobileNo(mobileNo)
                .snsType(user.getSnsType())
                .refreshToken(user.getSnsInfo().getRefreshToken())
                .address(user.getUserBasic().getAddress())
                .gender(user.getUserBasic().getGender())
                .birth(user.getUserBasic().getBirth())
                .build());

        //  when
        MockHttpServletResponse response = mockMvc.perform(post("/users")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        //  then
        assertThat(response.getStatus()).isEqualTo(ErrorCode.VALIDATION_ERROR_PHONE_NUMBER.getHttpStatus().value());
        assertThat(response.getContentAsString()).isEqualTo(ErrorCode.VALIDATION_ERROR_PHONE_NUMBER.getDetail());
    }

    @DisplayName("회원가입 실패 - 비어있는 PhoneNumber")
    @ParameterizedTest
    @NullAndEmptySource
    void joinFailPhoneNumberValidationEmptyValue(String mobileNo) throws Exception {
        //  given
        String content = objectMapper.writeValueAsString(UserJoinDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .password(user.getPassword())
                .nickName(user.getNickName())
                .mobileNo(mobileNo)
                .snsType(user.getSnsType())
                .refreshToken(user.getSnsInfo().getRefreshToken())
                .address(user.getUserBasic().getAddress())
                .gender(user.getUserBasic().getGender())
                .birth(user.getUserBasic().getBirth())
                .build());

        //  when
        MockHttpServletResponse response = mockMvc.perform(post("/users")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        //  then
        assertThat(response.getStatus()).isEqualTo(ErrorCode.VALIDATION_ERROR_PHONE_NUMBER_EMPTY.getHttpStatus().value());
        assertThat(response.getContentAsString()).isEqualTo(ErrorCode.VALIDATION_ERROR_PHONE_NUMBER_EMPTY.getDetail());
    }
}