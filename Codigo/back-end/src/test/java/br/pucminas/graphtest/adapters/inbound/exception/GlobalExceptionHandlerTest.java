package br.pucminas.graphtest.adapters.inbound.exception;

import br.pucminas.graphtest.adapters.inbound.error.ErrorResponse;
import br.pucminas.graphtest.application.exception.InvalidCyclomaticComplexityException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldReturnNotFoundForUnknownEndpoint() {
        NoResourceFoundException exception = new NoResourceFoundException(HttpMethod.GET, "/inexistente", null);

        var response = handler.handleNoResource(exception);
        ErrorResponse body = (ErrorResponse) response.getBody();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND.value(), body.getStatus());
    }

    @Test
    void shouldWriteUnauthorizedResponseOnAuthenticationFailure() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationFailure(request, response, new BadCredentialsException("credenciais invalidas"));

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("\"status\": 401"));
    }

    @Test
    void shouldReturnBadRequestForInvalidCyclomaticComplexity() {
        var exception = new InvalidCyclomaticComplexityException("Complexidade invalida");

        var response = handler.handleBadRequest(exception);
        ErrorResponse body = (ErrorResponse) response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.getStatus());
    }
}
