package br.pucminas.graphtest.application.exception;


public class AtualizarSenhaException extends ConflictException {

    public AtualizarSenhaException(String mensagem) {
        super(mensagem);
    }
}
