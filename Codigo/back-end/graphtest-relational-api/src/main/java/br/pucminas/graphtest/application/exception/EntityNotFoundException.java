package br.pucminas.graphtest.application.exception;

public class EntityNotFoundException extends NotFoundException {

    public EntityNotFoundException(String mensagem) {
        super(mensagem);
    }
}
