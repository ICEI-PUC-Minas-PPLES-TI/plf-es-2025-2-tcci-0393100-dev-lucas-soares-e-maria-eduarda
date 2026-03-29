package br.pucminas.graphtest.application.exception;


public class TopicNotFoundException extends NotFoundException{

    public TopicNotFoundException(String mensagem) {
        super(mensagem);
    }
}
