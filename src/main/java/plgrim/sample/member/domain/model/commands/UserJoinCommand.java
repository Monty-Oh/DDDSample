package plgrim.sample.member.domain.model.commands;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import plgrim.sample.member.domain.model.valueobjects.UserBasic;

@Builder
@Getter
@ToString
public class UserJoinCommand {
    private String email;
    private String password;
    private String phoneNumber;
    private UserBasic userBasic;
}
