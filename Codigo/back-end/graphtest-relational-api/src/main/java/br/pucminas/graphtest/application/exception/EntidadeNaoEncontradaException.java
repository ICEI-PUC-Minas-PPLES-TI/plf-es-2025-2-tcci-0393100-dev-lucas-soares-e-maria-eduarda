package br.pucminas.graphtest.application.exception;

public class EntidadeNaoEncontradaException extends NotFoundException {

    public EntidadeNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}
