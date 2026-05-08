package br.pucminas.graphtest.application.exception;

public class DecisionTableAlreadyExistsException extends ConflictException {

    public DecisionTableAlreadyExistsException(String message) {
        super(message);
    }
}
