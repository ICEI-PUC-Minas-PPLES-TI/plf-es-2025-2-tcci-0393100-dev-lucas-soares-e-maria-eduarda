package br.pucminas.graphtest.application.exception;

public abstract class AuthorizationException extends ApplicationException {

    public AuthorizationException(String message) {
        super(message);
    }

}