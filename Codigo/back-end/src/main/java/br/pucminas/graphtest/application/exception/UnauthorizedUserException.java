package br.pucminas.graphtest.application.exception;


public class UnauthorizedUserException extends AuthorizationException {

    public UnauthorizedUserException(String mensagem) {
        super(mensagem);
    }
}
