package plgrim.sample.member.domain.model.aggregates;

import lombok.*;
import plgrim.sample.member.domain.model.valueobjects.UserBasic;

import javax.persistence.*;

@Entity
@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    /**
     * @PrimaryKey
     * USR_NO 자동 생성
     * */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long usrNo;

    private String email;

    @Column(name = "PWD")
    private String password;

    @Column(name = "PHN_NUM")
    private String phoneNumber;

    @Embedded
    private UserBasic userBasic;

    // 패스워드 변경
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    public void changePhoneNumber(String newPhoneNumber) {
        this.phoneNumber = newPhoneNumber;
    }

    public void changeUserBasic(UserBasic userBasic) {
        this.userBasic = userBasic;
    }
}
