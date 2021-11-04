package plgrim.sample.member.controller.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import plgrim.sample.member.controller.validation.EmailValidation;
import plgrim.sample.member.controller.validation.PasswordValidation;

@Getter
@Builder
@AllArgsConstructor
public class UserIdLoginDTO {
    private String id;

    @PasswordValidation(min = 5, max = 20)
    private String password;
}
