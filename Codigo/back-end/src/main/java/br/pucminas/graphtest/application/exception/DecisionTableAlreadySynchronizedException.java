package br.pucminas.graphtest.application.exception;

public class DecisionTableAlreadySynchronizedException extends ConflictException {

    public DecisionTableAlreadySynchronizedException(String message) {
        super(message);
    }
}
