package br.pucminas.graphtest.adapters.inbound.security;

import br.pucminas.graphtest.infrastructure.security.JWT;
import br.pucminas.graphtest.adapters.outbound.security.CustomUserDetails;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import java.io.IOException;
import static br.pucminas.graphtest.adapters.inbound.util.ConstantesRequisicaoUtil.*;
import static br.pucminas.graphtest.infrastructure.util.ConstantesTopicosUtil.JWT_FILTRO_AUTORIZACAO;
import static java.util.Objects.nonNull;
import static org.springframework.util.StringUtils.trimAllWhitespace;

@Slf4j(topic = JWT_FILTRO_AUTORIZACAO)
public class JWTFiltroAutorizacao extends BasicAuthenticationFilter {

    private final CustomUserDetails userDetailsServiceImpl;
    private final JWT jwt;

    public JWTFiltroAutorizacao(AuthenticationManager authenticationManager, JWT jwt, CustomUserDetails userDetailsServiceImpl) {
        super(authenticationManager);
        this.jwt = jwt;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    /**
     * Realiza filtro de autorização de usuário
     *
     * @param request     requisição
     * @param response    resposta
     * @param filterChain camada de segurança Filter Chain
     */
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("===========================================================================");
        log.info(">>> doFilterInternal: realizando filtro de autorização");
        String headerAutorizacao = request.getHeader(HEADER_AUTORIZACAO);
        if (nonNull(headerAutorizacao) && headerAutorizacao.startsWith(TIPO_TOKEN)) {
            String token = trimAllWhitespace(headerAutorizacao).substring(TIPO_TOKEN.length());
            UsernamePasswordAuthenticationToken auth = getAutenticacao(token);
            if (nonNull(auth))
                SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Método responsável por receber um token e retornar uma autenticação
     *
     * @param token token do usuário
     * @return autenticação
     */
    private @Nullable UsernamePasswordAuthenticationToken getAutenticacao(String token) {
        log.info(">>> getAutenticacao: obtendo autenticação do usuário");
        if (this.jwt.tokenValido(token)) {
            String emailUsuario = this.jwt.getEmailUsuario(token);
            UserDetails usuario = this.userDetailsServiceImpl.loadUserByUsername(emailUsuario);
            return new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
        }
        return null;
    }
}
