package br.pucminas.graphtest.application.exception;


public class DeleteEntityException extends ConflictException {

    public DeleteEntityException(String mensagem) {
        super(mensagem);
    }
}