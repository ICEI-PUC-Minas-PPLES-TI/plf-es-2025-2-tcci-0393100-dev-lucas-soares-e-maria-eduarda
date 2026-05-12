package br.pucminas.graphtest.application.exception;

public class GfcSourceFileProjectMismatchException extends AuthorizationException {

    public GfcSourceFileProjectMismatchException() {
        super("O arquivo-fonte informado nao pertence ao projeto informado.");
    }
}
