package plgrim.sample.common.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.member.domain.model.entities.UserRole;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@ConfigurationProperties(prefix = "token")
@Setter
@RequiredArgsConstructor
@Component
public class LocalTokenProvider implements TokenProvider {

    private String secretKey;
    private final Long validTime = 1000L * 60 * 60;     //  토큰 유효시간 30분

    private final UserDetailsService userDetailsService;

    //  객체 초기화, secretKey를 Base64로 인코딩
    //  의존성 주입이 이루어진 후 초기화하는 작업
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    //  JWT 토큰 생성
    public String createToken(String userPk, List<UserRole> roles) {
        Claims claims = Jwts.claims().setSubject(userPk);     //  JWT payload 에 저장되는 정보 단위
        claims.put("roles", roles);     //  정보는 key : value 쌍으로 저장

        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)  //  정보저장
                .setIssuedAt(now)   //  토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + validTime))    //  만료시간
                .signWith(SignatureAlgorithm.HS256, secretKey)  //  암호화 알고리즘, 키 값 세팅
                .compact();
    }

    @Override
    public Sns getThisTargetSns() {
        return Sns.LOCAL;
    }

    // JWT 토큰에서 인증 정보 조회
    @Override
    public Authentication getAuthentication(String token) {
        //  UserDetailService 에 사용자 정보(아이디)를 넘겨준다.
        //  넘겨받은 사용자 정보를 통해 DB에서 찾은 사용자 정보인 UserDetails 객체를 생성한다.
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserInfo(token));
        //  새로운 인증용 객체를 반환한다.
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 회원 정보 추출
    @Override
    public String getUserInfo(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 토큰의 유효성 + 만료일자 확인
    @Override
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(jwtToken);

            return !claims.getBody()
                    .getExpiration()
                    .before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
