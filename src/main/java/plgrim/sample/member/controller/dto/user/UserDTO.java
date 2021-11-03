package plgrim.sample.member.controller.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.member.domain.model.valueobjects.SnsInfo;
import plgrim.sample.member.domain.model.valueobjects.UserBasic;

@Getter
@Builder
@AllArgsConstructor
@ToString
public class UserDTO {
    private Long usrNo;
    private String userId;
    private String email;
    private String nickName;

    private String mobileNo;

    private Sns snsType;
    private SnsInfo snsInfo;
    private UserBasic userBasic;
}
