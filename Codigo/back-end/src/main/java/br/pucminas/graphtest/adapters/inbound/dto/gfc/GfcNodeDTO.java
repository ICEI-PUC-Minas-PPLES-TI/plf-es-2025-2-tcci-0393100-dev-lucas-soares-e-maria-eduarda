package br.pucminas.graphtest.adapters.inbound.dto.gfc;

import br.pucminas.graphtest.application.domain.gfc.enums.GfcNodeTypeEnum;

import java.util.UUID;

/**
 * Response de um no do GFC.
 */
public record GfcNodeDTO(
        UUID id,
        String code,
        String label,
        GfcNodeTypeEnum type,
        Integer startLine,
        Integer endLine
) {
}
