package plgrim.sample.member.controller.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import plgrim.sample.common.enums.Gender;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.member.controller.validation.EmailValidation;
import plgrim.sample.member.controller.validation.PasswordValidation;
import plgrim.sample.member.controller.validation.PhoneNumberValidation;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class UserModifyDTO {

    @EmailValidation
    private String email;

    @PasswordValidation(min = 5, max = 20)
    private String password;

    private String nickName;

    private String mobileNo;

    /**
     * SnsInfo.class
     * */
    private String refreshToken;

    private String address;

    private Gender gender;

    private LocalDate birth;
}
