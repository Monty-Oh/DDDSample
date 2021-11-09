package plgrim.sample.member.domain.model.commands;

import lombok.Builder;
import lombok.Getter;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.member.domain.model.entities.SnsInfo;
import plgrim.sample.member.domain.model.valueobjects.UserBasic;

@Builder
@Getter
public class UserModifyCommand {
    private String userId;
    private String email;
    private String password;
    private String nickName;
    private String mobileNo;
    private Sns snsType;
    private SnsInfo snsInfo;
    private UserBasic userBasic;
}
