package plgrim.sample.member.domain.model.valueobjects;

import lombok.*;
import plgrim.sample.common.enums.Sns;

import javax.persistence.Embeddable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@ToString
@Getter
public class SnsInfo {
    private String refreshToken;
    private String tokenType;
    private String scope;
    public void changeRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
