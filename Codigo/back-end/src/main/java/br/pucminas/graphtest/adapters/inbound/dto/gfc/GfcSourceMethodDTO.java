package br.pucminas.graphtest.adapters.inbound.dto.gfc;

/**
 * Response com metadados de um metodo Java disponivel para geracao de GFC.
 */
public record GfcSourceMethodDTO(
        String name,
        String signature,
        Integer startLine,
        Integer endLine
) {
}
