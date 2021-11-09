package plgrim.sample.member.domain.model.entities;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Getter
public class SnsInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private Long uid;
    private String refreshToken;
    private String tokenType;
    private String scope;

    public void changeRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
