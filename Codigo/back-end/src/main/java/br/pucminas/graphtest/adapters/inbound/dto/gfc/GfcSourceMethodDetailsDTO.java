package br.pucminas.graphtest.adapters.inbound.dto.gfc;

public record GfcSourceMethodDetailsDTO(
        String name,
        String signature,
        Integer startLine,
        Integer endLine,
        String sourceCode
) {
}
