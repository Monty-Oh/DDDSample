package plgrim.sample.member.domain.model.valueobjects;

import lombok.*;
import plgrim.sample.common.enums.Gender;
import plgrim.sample.common.enums.Sns;

import javax.persistence.Embeddable;
import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@ToString
@Getter
public class UserBasic {
    private String address;
    private Gender gender;
    private LocalDate birth;
    private Sns snsType;
}
