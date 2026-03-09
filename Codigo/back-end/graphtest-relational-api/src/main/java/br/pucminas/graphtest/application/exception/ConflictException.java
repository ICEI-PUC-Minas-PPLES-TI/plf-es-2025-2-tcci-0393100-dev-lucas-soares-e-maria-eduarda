package br.pucminas.graphtest.application.exception;

public abstract class ConflictException extends ApplicationException {

    public ConflictException(String message) {
        super(message);
    }

}
