package br.pucminas.graphtest.application.exception;


public class UpdatePasswordException extends ConflictException {

    public UpdatePasswordException(String mensagem) {
        super(mensagem);
    }
}
