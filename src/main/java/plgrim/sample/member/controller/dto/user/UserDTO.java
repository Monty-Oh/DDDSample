package plgrim.sample.member.controller.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import plgrim.sample.member.domain.model.aggregates.User;
import plgrim.sample.member.domain.model.valueobjects.UserBasic;

@Getter
@Builder
@AllArgsConstructor
@ToString
public class UserDTO {
    private String email;
    private String phoneNumber;
    private UserBasic userBasic;
}
