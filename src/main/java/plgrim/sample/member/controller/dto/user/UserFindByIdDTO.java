package plgrim.sample.member.controller.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import plgrim.sample.member.controller.validation.EmailValidation;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class UserFindByIdDTO {
    @EmailValidation
    private String email;
}
