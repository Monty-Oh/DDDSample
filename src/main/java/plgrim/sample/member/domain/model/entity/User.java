package plgrim.sample.member.domain.model.entity;

import lombok.*;
import plgrim.sample.member.domain.model.vo.UserBasic;

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
     * id, 이메일 기반
     * */
    @Id
    private String id;

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
