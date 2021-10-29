package plgrim.sample.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Sns {
    GOOGLE("google"),
    NAVER("naver"),
    KAKAO("kakao"),
    APPLE("apple"),
    LOCAL("local");

    private final String value;
}
