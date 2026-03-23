package br.pucminas.graphtest.application.port.output.security;

import java.util.UUID;

public interface TokenServicePort {

    String gerarToken(String email, String role, UUID userId);

    boolean tokenValido(String token);

    String getEmailUsuario(String token);
}
