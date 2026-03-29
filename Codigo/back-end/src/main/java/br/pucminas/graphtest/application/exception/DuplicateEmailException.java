package br.pucminas.graphtest.application.exception;

public class DuplicateEmailException extends ConflictException {

    public DuplicateEmailException(String mensagem) {
        super(mensagem);
    }
}
