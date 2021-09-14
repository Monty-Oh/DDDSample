package plgrim.sample.member.controller.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import plgrim.sample.common.enums.Gender;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.member.domain.model.entity.User;
import plgrim.sample.member.domain.model.vo.UserBasic;

import java.time.LocalDate;

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
