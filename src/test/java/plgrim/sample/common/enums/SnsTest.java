package plgrim.sample.common.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SnsTest {

    @Test
    void findSnsByValueFail() {
        String message = assertThrows(IllegalArgumentException.class, () -> Sns.findSnsByValue("test")).getMessage();

        assertThat(message).isEqualTo("해당 Sns 타입을 찾을 수 없습니다.");
    }
}