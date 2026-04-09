package br.pucminas.graphtest.adapters.inbound.security;

import br.pucminas.graphtest.application.security.AuthenticatedUser;
import br.pucminas.graphtest.application.port.input.security.AuthenticatedUserByTokenUseCasePort;
import jakarta.annotation.Nullable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import java.io.IOException;
import java.util.List;
import static br.pucminas.graphtest.adapters.inbound.util.SecurityHttpConstantsUtil.HEADER_AUTORIZACAO;
import static br.pucminas.graphtest.adapters.inbound.util.SecurityHttpConstantsUtil.TIPO_TOKEN;
import static br.pucminas.graphtest.shared.LogTopicsUtil.JWT_AUTHORIZATION_FILTER;
import static java.util.Objects.nonNull;
import static org.springframework.util.StringUtils.trimAllWhitespace;

@Slf4j(topic = JWT_AUTHORIZATION_FILTER)
public class JwtAuthorizationFilterAdapter extends BasicAuthenticationFilter {

    private final AuthenticatedUserByTokenUseCasePort authenticatedUserByTokenUseCasePort;

    public JwtAuthorizationFilterAdapter(
            AuthenticationManager authenticationManager,
            AuthenticatedUserByTokenUseCasePort authenticatedUserByTokenUseCasePort
    ) {
        super(authenticationManager);
        this.authenticatedUserByTokenUseCasePort = authenticatedUserByTokenUseCasePort;
    }

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        log.info("===========================================================================");
        log.info(">>> doFilterInternal: realizando filtro de autorizacao");

        String headerAutorizacao = request.getHeader(HEADER_AUTORIZACAO);
        if (nonNull(headerAutorizacao) && headerAutorizacao.startsWith(TIPO_TOKEN)) {
            String token = trimAllWhitespace(headerAutorizacao).substring(TIPO_TOKEN.length());
            UsernamePasswordAuthenticationToken auth = getAutenticacao(token);
            if (nonNull(auth)) {
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }

    private @Nullable UsernamePasswordAuthenticationToken getAutenticacao(String token) {
        log.info(">>> getAutenticacao: obtendo autenticacao do usuario");

        AuthenticatedUser authenticatedUser = authenticatedUserByTokenUseCasePort.execute(token);
        if (authenticatedUser != null) {
            return new UsernamePasswordAuthenticationToken(
                    authenticatedUser,
                    null,
                    List.of(new SimpleGrantedAuthority(authenticatedUser.profile().getDescricao()))
            );
        }

        return null;
    }
}
