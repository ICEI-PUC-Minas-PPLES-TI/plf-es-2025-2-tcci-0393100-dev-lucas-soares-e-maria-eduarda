package br.pucminas.graphtest.application.exception;

public class GfcSourceFileNotFoundException extends NotFoundException {

    public GfcSourceFileNotFoundException() {
        super("Arquivo-fonte GFC nao encontrado.");
    }
}
