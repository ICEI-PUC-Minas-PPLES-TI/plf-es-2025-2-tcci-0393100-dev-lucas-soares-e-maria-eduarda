package br.pucminas.graphtest.application.exception;

public class InvalidNodeException extends ConflictException{

    public InvalidNodeException(String mensagem) {
        super(mensagem);
    }
}
