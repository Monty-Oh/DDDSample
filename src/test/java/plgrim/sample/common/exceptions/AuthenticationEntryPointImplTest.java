package plgrim.sample.common.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;
import plgrim.sample.common.enums.ErrorCode;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class AuthenticationEntryPointImplTest {
    AuthenticationEntryPointImpl authenticationEntryPoint = new AuthenticationEntryPointImpl();

    @Test
    void commence() throws IOException {
        //  given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setCharacterEncoding("utf-8");
        AuthenticationException exception = new AuthenticationCredentialsNotFoundException("test");

        //  when
        authenticationEntryPoint.commence(request, response, exception);

        //  then
        assertThat(response.getStatus()).isEqualTo(ErrorCode.UNAUTHORIZED.getHttpStatus().value());
        assertThat(response.getContentAsString()).isEqualTo(ErrorCode.UNAUTHORIZED.getDetail());
    }
}