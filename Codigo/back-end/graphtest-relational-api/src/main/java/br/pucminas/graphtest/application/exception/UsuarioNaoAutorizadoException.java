package br.pucminas.graphtest.application.exception;


public class UsuarioNaoAutorizadoException extends AuthorizationException {

    public UsuarioNaoAutorizadoException(String mensagem) {
        super(mensagem);
    }
}
