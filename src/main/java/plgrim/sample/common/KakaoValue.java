package plgrim.sample.common;

public class KakaoValue {
    public static final String KAPI_REST_API = "03daa4391ed176013bd17b15f7ad39c1";
    public static final String KAPI_API_REDIRECT_LOGIN_URL = "http://localhost:8080/login/kakao/";

    /**
     * 카카오 로그인 페이지
     * https://kauth.kakao.com/oauth/authorize
     */
    public static final String KAKAO_LOGIN_PAGE_URL = "https://kauth.kakao.com/oauth/authorize";

    /**
     * 카카오 토큰 발급 URL
     * https://kauth.kakao.com/oauth/token
     */
    public static final String KAPI_GET_TOKEN_URL = "https://kauth.kakao.com/oauth/token";

    /**
     * 카카오 엑세스 토큰 검증 URL
     * https://kapi.kakao.com/v1/user/access_token_info
     */
    public static final String KAPI_CHECK_ACCESS_TOKEN_URL = "https://kapi.kakao.com/v1/user/access_token_info";

    /**
     * 카카오 회원 정보 조회 URL
     * https://kapi.kakao.com/v2/user/me
     */
    public static final String KAPI_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";
}
