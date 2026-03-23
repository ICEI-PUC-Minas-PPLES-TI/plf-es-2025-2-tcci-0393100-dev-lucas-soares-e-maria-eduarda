package br.pucminas.graphtest.adapters.outbound.security;

import br.pucminas.graphtest.application.port.output.security.TokenServicePort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

import static br.pucminas.graphtest.shared.logging.LogTopics.JWT;
import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.nonNull;

@Slf4j(topic = JWT)
@Component
public class JWTAdapter implements TokenServicePort {

    @Value("${jwt.segredo}")
    private String jwtSegredo;

    @Value("${jwt.tempo_expiracao}")
    private Long tempoExpiracao;

    @Override
    public String gerarToken(String email, String role, UUID userId) {
        log.info(">>> gerarToken: gerando token de autenticacao");
        SecretKey chave = gerarChaveSegredo();
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .claim("userId", userId)
                .setExpiration(new Date(System.currentTimeMillis() + tempoExpiracao))
                .signWith(chave)
                .compact();
    }

    private @NotNull SecretKey gerarChaveSegredo() {
        return hmacShaKeyFor(this.jwtSegredo.getBytes());
    }

    @Override
    public boolean tokenValido(String token) {
        log.info(">>> tokenValido: verificando validade do token");
        Claims claims = getClaims(token);
        if (nonNull(claims)) {
            String nome = claims.getSubject();
            Date dataExpiracaoToken = claims.getExpiration();
            Date dataAtual = new Date(currentTimeMillis());
            return nonNull(nome) && nonNull(dataExpiracaoToken) && dataAtual.before(dataExpiracaoToken);
        }
        log.info(">>> tokenValido: token expirado");
        return false;
    }

    @Override
    public String getEmailUsuario(String token) {
        log.info(">>> getEmailUsuario: obtendo email do usuario ativo");
        Claims claims = getClaims(token);
        return nonNull(claims) ? claims.getSubject() : null;
    }

    private @Nullable Claims getClaims(String token) {
        SecretKey chave = gerarChaveSegredo();
        try {
            return Jwts.parser()
                    .verifyWith(chave)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.warn(">>> getClaims: token invalido. Motivo: {}", e.getMessage());
            return null;
        }
    }
}
