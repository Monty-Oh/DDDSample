package plgrim.sample.member.controller.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import plgrim.sample.common.enums.Gender;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.member.controller.validation.IdValidation;
import plgrim.sample.member.controller.validation.PasswordValidation;
import plgrim.sample.member.controller.validation.PhoneNumberValidation;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class UserJoinDTO {
    @IdValidation
    private String id;

    @PasswordValidation(min = 5, max = 20)
    private String password;

    private String phoneNumber;

    private String address;

    private Gender gender;

    private LocalDate birth;

    private Sns SnsType;
}
