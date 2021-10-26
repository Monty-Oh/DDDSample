package plgrim.sample.member.infrastructure.rest.dto;

import lombok.*;

import java.util.Date;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class KakaoUserInfoDTO {
    //  회원번호
    private Long id;
    //  false: 연결 대기 상태 true: 연결 상태
    // 자동 연결 설정을 비활성한 경우에만 존재함.
    private Boolean has_signed_up;
    //  서비스에 연결 완료된 시각 UTC
    private Date connected_at;
    //  카카오 싱크 간편 가입을 통해 로그인한 시각, UTC
    private Date synched_at;
    //  사용자 프로퍼티
    private Map<String, String> properties;
    // 카카오 계정 정보
    private Map<String, Object> kakao_account;
}