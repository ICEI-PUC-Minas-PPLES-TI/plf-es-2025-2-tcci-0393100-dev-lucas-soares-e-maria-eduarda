package br.pucminas.graphtest.adapters.inbound.dto.gfc;

import java.util.UUID;

public record CreateGfcSourceFileResponseDTO(
        UUID id_arquivo,
        String mensagem,
        Integer status
) {
}
