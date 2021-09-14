package plgrim.sample.member.controller.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import plgrim.sample.common.enums.Gender;
import plgrim.sample.common.enums.Sns;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class UserModifyDTO {
    private String id;

    private String password;

    private String phoneNumber;

    private String address;

    private Gender gender;

    private LocalDate birth;

    private Sns SnsType;
}
