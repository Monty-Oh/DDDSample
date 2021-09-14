package plgrim.sample.member.controller.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import plgrim.sample.member.domain.model.entity.User;

@Getter
@AllArgsConstructor
public class UserFindByUserNoDTO {
    private Long UserNo;
}
