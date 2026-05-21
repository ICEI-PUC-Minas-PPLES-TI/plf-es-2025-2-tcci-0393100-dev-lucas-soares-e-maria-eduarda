package br.pucminas.graphtest.adapters.inbound.dto.gfc;

import java.util.UUID;

/**
 * Response de criacao persistida de um Grafo de Fluxo de Controle.
 */
public record CreateGfcResponseDTO(
        UUID id_gfc,
        String mensagem,
        Integer status
) {
}
