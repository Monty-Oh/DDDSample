package plgrim.sample.common.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import plgrim.sample.common.LocalTokenProvider;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("JwtAuthenticationFilter 테스트")
@SpringBootTest
class JwtAuthenticationFilterTest {

    @Autowired
    LocalTokenProvider localTokenProvider;

    private LocalAuthenticationFilter localAuthenticationFilter;

    @BeforeEach
    void setup() {
        localAuthenticationFilter = new LocalAuthenticationFilter(localTokenProvider);
    }

    @DisplayName("토큰 인증 필터(doFilter) 테스트")
    @Test
    void doFilter() throws ServletException, IOException {
        //  given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-AUTH-TOKEN", "token resolve test");
        request.addHeader("X-SNS-TYPE", "local");
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        MockFilterChain mockFilterChain = Mockito.mock(MockFilterChain.class);

        //  when    //  then
        assertDoesNotThrow(() -> localAuthenticationFilter.doFilter(request, response, mockFilterChain));
    }

    @DisplayName("토큰 인증 필터(doFilter) 테스트 - 토큰 없음. 필터 패스")
    @Test
    void doFilterNotHasToken() throws ServletException, IOException {
        //  given
        MockHttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        MockFilterChain mockFilterChain = Mockito.mock(MockFilterChain.class);

        //  when    //  then
        assertDoesNotThrow(() -> localAuthenticationFilter.doFilter(request, response, mockFilterChain));
    }
}