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
    private Long userNo;
    private String id;
    private String password;
    private String phoneNumber;
    private UserBasic userBasic;

    public UserDTO(User user) {
        this.id = user.getId();
        this.password = user.getPassword();
        this.phoneNumber = user.getPhoneNumber();
        this.userBasic = user.getUserBasic();
    }
}
