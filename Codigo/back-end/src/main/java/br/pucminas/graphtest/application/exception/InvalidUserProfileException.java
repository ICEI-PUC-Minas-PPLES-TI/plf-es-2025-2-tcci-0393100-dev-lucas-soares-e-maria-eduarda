package br.pucminas.graphtest.application.exception;

public class InvalidUserProfileException extends ConflictException {

    public InvalidUserProfileException(String mensagem) {
        super(mensagem);
    }
}
