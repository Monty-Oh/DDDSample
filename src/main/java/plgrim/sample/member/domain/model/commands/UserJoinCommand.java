package plgrim.sample.member.domain.model.commands;

import lombok.Builder;
import lombok.Getter;
import plgrim.sample.member.domain.model.valueobjects.UserBasic;

@Builder
@Getter
public class UserJoinCommand {
    private String email;
    private String password;
    private String phoneNumber;
    private UserBasic userBasic;
}
