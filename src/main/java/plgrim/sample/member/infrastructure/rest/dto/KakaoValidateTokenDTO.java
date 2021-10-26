package plgrim.sample.member.infrastructure.rest.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class KakaoValidateTokenDTO {
    //  회원 번호
    private Long id;
    //  엑세스 토큰 만료 시간(초)
    private Integer expires_in;
    //  토큰이 발급된 앱의 ID
    private Integer app_id;
}
