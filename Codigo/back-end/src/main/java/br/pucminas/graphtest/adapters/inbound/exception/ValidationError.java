package br.pucminas.graphtest.adapters.inbound.exception;

public record ValidationError(String campo, String mensagem) {
}

