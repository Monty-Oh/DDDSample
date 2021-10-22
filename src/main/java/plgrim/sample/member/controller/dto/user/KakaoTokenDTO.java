package plgrim.sample.member.controller.dto.user;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class KakaoTokenDTO {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private Long expires_in;
    private String scope;
    private Long refresh_token_expires_in;
}
