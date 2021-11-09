package plgrim.sample.member.domain.model.aggregates;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import plgrim.sample.common.enums.Sns;
import plgrim.sample.member.domain.model.entities.SnsInfo;
import plgrim.sample.member.domain.model.entities.UserRole;
import plgrim.sample.member.domain.model.valueobjects.UserBasic;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    /**
     * @PrimaryKey USR_NO 자동 생성
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long usrNo;

    /**
     * Local 로그인 ID,
     * Sns 의 경우 고유 "ID값" + "_Sns String value"
     * ex) 010101010_kakao
     * 유일키
     */
    private String userId;

    /**
     * 회원 이메일
     */
    private String email;

    private String password;

    /**
     * 유일키
     */
    private String nickName;

    private String mobileNo;

    private Sns snsType;

    //    @Embedded
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "uid")
    private SnsInfo snsInfo;

    @Embedded
    private UserBasic userBasic;

    /**
     * fetchType 체크하기
     * 모르면 Lazy 쓰는게 좋다.
     * */
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "uid")
    private List<UserRole> roles;

    // 패스워드 변경
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    public void changePhoneNumber(String newPhoneNumber) {
        this.mobileNo = newPhoneNumber;
    }

    public void changeUserBasic(UserBasic userBasic) {
        this.userBasic = userBasic;
    }

    public String getPassword() {
        return this.password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map((UserRole role) -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toList());
    }


    @Override
    public String getUsername() {
        return userId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
