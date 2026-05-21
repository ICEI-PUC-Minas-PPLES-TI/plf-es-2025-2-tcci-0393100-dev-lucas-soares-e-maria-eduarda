package br.pucminas.graphtest.application.port.input.gfc.records;

/**
 * Saida com metadados de um metodo Java disponivel para geracao de GFC.
 */
public record GfcSourceMethodOutput(
        String name,
        String signature,
        Integer startLine,
        Integer endLine
) {
}
