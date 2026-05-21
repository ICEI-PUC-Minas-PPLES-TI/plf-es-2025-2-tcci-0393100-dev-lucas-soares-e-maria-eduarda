package br.pucminas.graphtest.adapters.inbound.dto.gfc;

/**
 * Response de remocao de um Grafo de Fluxo de Controle.
 */
public record DeleteGfcResponseDTO(
        String mensagem,
        Integer status
) {
}
