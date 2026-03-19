package br.pucminas.graphtest.adapters.inbound.security;

import br.pucminas.graphtest.adapters.inbound.dto.LoginRequestDTO;
import br.pucminas.graphtest.application.port.input.security.GenerateTokenUseCase;
import br.pucminas.graphtest.application.port.input.security.command.GenerateTokenCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import static br.pucminas.graphtest.adapters.inbound.util.SecurityHttpConstantsUtil.CHARACTER_ENCODING;
import static br.pucminas.graphtest.adapters.inbound.util.SecurityHttpConstantsUtil.CONTENT_TYPE;
import static br.pucminas.graphtest.adapters.inbound.util.SecurityHttpConstantsUtil.CORPO_RESPOSTA_REQUISICAO;
import static br.pucminas.graphtest.adapters.inbound.util.SecurityHttpConstantsUtil.HEADER_AUTORIZACAO;
import static br.pucminas.graphtest.adapters.inbound.util.SecurityHttpConstantsUtil.VALOR_HEADER_AUTORIZACAO;
import static br.pucminas.graphtest.shared.logging.LogTopics.JWT_AUTHENTICATION_FILTER;
import static java.lang.String.format;

@Slf4j(topic = JWT_AUTHENTICATION_FILTER)
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final GenerateTokenUseCase generateTokenUseCase;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, GenerateTokenUseCase generateTokenUseCase) {
        this.authenticationManager = authenticationManager;
        this.generateTokenUseCase = generateTokenUseCase;
    }

    @Override
    public Authentication attemptAuthentication(@NotNull HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        LoginRequestDTO loginRequest;
        try {
            loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequestDTO.class);
        } catch (IOException e) {
            throw new RuntimeException(format("[ERRO] IOException: falha ao ler autenticacao do usuario: %s", e));
        }

        log.info("=========================================================================");
        log.info(format(">>> attemptAuthentication: realizando autenticacao do usuario: %s", loginRequest.email()));

        UsernamePasswordAuthenticationToken tokenAuth = new UsernamePasswordAuthenticationToken(
                loginRequest.email(),
                loginRequest.password(),
                new ArrayList<>()
        );

        return this.authenticationManager.authenticate(tokenAuth);
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            @NotNull HttpServletResponse response,
            FilterChain filterChain,
            @NotNull Authentication authentication
    ) {
        log.info(">>> successfulAuthentication: autenticacao realizada com sucesso");

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetailsAdapter authenticatedPrincipal)) {
            throw new IllegalStateException("Principal autenticado invalido");
        }

        String emailUsuario = authenticatedPrincipal.getUsername();
        String token = generateTokenUseCase.execute(new GenerateTokenCommand(
                authenticatedPrincipal.getId(),
                emailUsuario,
                authenticatedPrincipal.getPerfilUsuario()
        ));

        // O que é feito na Linha 84 a 86? R =
        response.addHeader(HEADER_AUTORIZACAO, format(VALOR_HEADER_AUTORIZACAO, token));
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding(CHARACTER_ENCODING);

        try {
            response.getWriter().write(format(CORPO_RESPOSTA_REQUISICAO, token, emailUsuario, LocalDateTime.now()));
        } catch (IOException e) {
            throw new RuntimeException(format("[ERRO] IOException: falha ao escrever headers de resposta: %s", e));
        }
    }
}
