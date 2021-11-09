package plgrim.sample.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum Sns {
    GOOGLE("google"),
    NAVER("naver"),
    KAKAO("kakao"),
    APPLE("apple"),
    LOCAL("local");

    private final String value;

    /**
     * unmodifiableMap : read-only Map
     */
    private static final Map<String, Sns> SNS_MAP = Collections.unmodifiableMap(Stream.of(values())
            .collect(Collectors.toMap(Sns::getValue, Function.identity())));

    /**
     * Sns value 조회
     */
    public static Sns findSnsByValue(String snsValue) {
        if (SNS_MAP.containsKey(snsValue)) {
            return SNS_MAP.get(snsValue);
        }
        throw new IllegalArgumentException("해당 Sns 타입을 찾을 수 없습니다.");
    }
}
