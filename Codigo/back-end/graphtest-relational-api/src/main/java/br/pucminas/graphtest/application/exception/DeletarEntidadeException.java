package br.pucminas.graphtest.application.exception;


public class DeletarEntidadeException extends ConflictException {

    public DeletarEntidadeException(String mensagem) {
        super(mensagem);
    }
}