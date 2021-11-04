package plgrim.sample;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SampleApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void applicationContextTest() {
        SampleApplication.main(new String[]{});
    }

}
