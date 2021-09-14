package plgrim.sample.member.controller.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import plgrim.sample.member.domain.model.entity.User;

@Getter
@Builder
@AllArgsConstructor
public class UserFindByIdDTO {
    private String id;
}
