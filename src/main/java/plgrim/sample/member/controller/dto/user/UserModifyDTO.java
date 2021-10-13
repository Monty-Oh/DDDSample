package plgrim.sample.member.controller.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import plgrim.sample.common.enums.Gender;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.member.controller.validation.EmailValidation;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@ToString
public class UserModifyDTO {
    private Long usrNo;

    @EmailValidation
    private String email;

    private String password;

    private String phoneNumber;

    private String address;

    private Gender gender;

    private LocalDate birth;

    private Sns snsType;
}
