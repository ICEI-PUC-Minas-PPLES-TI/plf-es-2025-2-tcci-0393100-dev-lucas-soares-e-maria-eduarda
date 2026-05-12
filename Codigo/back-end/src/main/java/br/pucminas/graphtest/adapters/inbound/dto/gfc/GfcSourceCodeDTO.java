package br.pucminas.graphtest.adapters.inbound.dto.gfc;

/**
 * Response com o conteudo completo de um arquivo Java enviado para a feature GFC.
 */
public record GfcSourceCodeDTO(
        String sourceCode
) {
}
