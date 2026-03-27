package br.pucminas.graphtest.application.exception;

public class InvalidEdgeTypeException extends ConflictException {

    public InvalidEdgeTypeException(String mensagem) {
        super(mensagem);
    }
}
