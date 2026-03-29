package br.pucminas.graphtest.application.exception;

public abstract class NotFoundException extends ApplicationException {

    public NotFoundException(String message) {
        super(message);
    }

}