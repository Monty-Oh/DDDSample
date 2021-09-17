package plgrim.sample.member.domain.model.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import plgrim.sample.common.enums.Gender;
import plgrim.sample.common.enums.Sns;

import javax.persistence.Embeddable;
import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class UserBasic {
    private String address;
    private Gender gender;
    private LocalDate birth;
    private Sns snsType;
}
