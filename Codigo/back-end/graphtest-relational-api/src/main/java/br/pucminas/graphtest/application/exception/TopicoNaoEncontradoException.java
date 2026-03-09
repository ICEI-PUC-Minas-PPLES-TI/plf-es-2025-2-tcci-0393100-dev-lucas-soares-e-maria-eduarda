package br.pucminas.graphtest.application.exception;


public class TopicoNaoEncontradoException extends RuntimeException{

    public TopicoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
