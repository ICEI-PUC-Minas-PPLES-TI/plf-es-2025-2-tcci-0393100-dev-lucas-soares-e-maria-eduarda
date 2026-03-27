package br.pucminas.graphtest.application.port.output.security;

public interface PasswordEncoderPort {

    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);

}