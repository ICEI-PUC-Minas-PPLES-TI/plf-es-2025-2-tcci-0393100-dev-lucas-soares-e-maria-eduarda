package br.pucminas.graphtest.application.port.input.gfc.records;

public record GfcSourceMethodDetailsOutput(
        String name,
        String signature,
        Integer startLine,
        Integer endLine,
        String sourceCode
) {
}
