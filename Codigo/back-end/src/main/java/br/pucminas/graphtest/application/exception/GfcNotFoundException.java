package br.pucminas.graphtest.application.exception;

public class GfcNotFoundException extends NotFoundException {

    public GfcNotFoundException() {
        super("Grafo de Fluxo de Controle nao encontrado.");
    }
}
