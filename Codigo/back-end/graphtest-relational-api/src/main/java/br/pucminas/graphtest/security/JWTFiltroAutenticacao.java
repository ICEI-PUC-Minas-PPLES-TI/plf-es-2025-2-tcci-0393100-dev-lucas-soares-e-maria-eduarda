package br.pucminas.graphtest.security;

import br.pucminas.graphtest.component.JWTComp;
import br.pucminas.graphtest.adapters.inbound.exception.GlobalExceptionHandler;
import br.pucminas.graphtest.domain.User;
import br.pucminas.graphtest.domain.enums.PerfilUsuario;
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
import tools.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import static br.pucminas.graphtest.util.ConstantesRequisicaoUtil.*;
import static br.pucminas.graphtest.util.ConstantesTopicosUtil.*;
import static java.lang.String.format;

@Slf4j(topic = JWT_FILTRO_AUTENTICACAO)
public class JWTFiltroAutenticacao extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTComp jwtComp;

    public JWTFiltroAutenticacao(AuthenticationManager authenticationManager, JWTComp jwtComp) {
        setAuthenticationFailureHandler(new GlobalExceptionHandler());
        this.authenticationManager = authenticationManager;
        this.jwtComp = jwtComp;
    }


    /**
     * Tenta realizar a autenticação do usuário pelo endpoint /login
     *
     * @param request  requisição
     * @param response resposta
     * @return autenticação
     * @throws AuthenticationException lança exceção caso haja erro durante a autenticação do usuário
     */
    @Override
    public Authentication attemptAuthentication(@NotNull HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        User usuario;
        try {
            usuario = new ObjectMapper().readValue(request.getInputStream(), User.class);
        } catch (IOException e) {
            throw new RuntimeException(format("[ERRO] IOException: falha ao ler autenticação do usuário: %s", e));
        }
        log.info("=========================================================================");
        log.info(format(">>> attemptAuthentication: realizando autenticação do usuário: %s", usuario.getEmail()));
        UsernamePasswordAuthenticationToken tokenAuth = new UsernamePasswordAuthenticationToken(usuario.getEmail(),
                usuario.getPassword (), new ArrayList<>());
        return this.authenticationManager.authenticate(tokenAuth);
    }

    /**
     * Formata os headers de resposta caso a autenticação seja realizada com sucesso
     *
     * @param request        requisição
     * @param response       resposta
     * @param filterChain    camada de segurança Filter Chain
     * @param authentication autenticação do usuário
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, @NotNull HttpServletResponse response, FilterChain filterChain, @NotNull Authentication authentication) {
        log.info(">>> successfulAuthentication: autenticação realizada com sucesso");
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
        String emailUsuario = userDetailsImpl.getUsername();
        PerfilUsuario roleUsuario = userDetailsImpl.getPerfilUsuario();
        UUID idUser = userDetailsImpl.getId();
        String token = this.jwtComp.gerarToken(emailUsuario, String.valueOf(roleUsuario).toLowerCase(), idUser);
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
