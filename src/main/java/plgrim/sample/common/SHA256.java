package plgrim.sample.common;

import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class SHA256 {
    // SHA256 암호화
    public String encrypt(String password) {
        try {
            // 비밀번호 암호화 객체
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

            // 지정된 바이트 데이터를 사용해 다이제스트를 갱신한다.
            messageDigest.update(password.getBytes());

            // 암호화를 리턴한다.
            return String.format("%064x", new BigInteger(1, messageDigest.digest()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
