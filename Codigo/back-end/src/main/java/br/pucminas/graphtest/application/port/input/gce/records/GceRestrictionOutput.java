package br.pucminas.graphtest.application.port.input.gce.records;

import br.pucminas.graphtest.application.domain.gce.model.GceRestriction;
import br.pucminas.graphtest.application.domain.gce.enums.RestrictionTypeEnum;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Saida de uma restricao do GCE.
 */
public record GceRestrictionOutput(
        UUID id,
        RestrictionTypeEnum type,
        List<String> nodeCodes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    /**
     * Converte uma restricao de dominio em sua representacao de saida.
     *
     * @param restriction restricao do dominio
     * @return saida correspondente
     */
    public static GceRestrictionOutput from(GceRestriction restriction) {
        return new GceRestrictionOutput(
                restriction.getId(),
                restriction.getType(),
                restriction.getNodeCodes(),
                restriction.getCreatedAt(),
                normalizeUpdatedAt(restriction.getCreatedAt(), restriction.getUpdatedAt())
        );
    }

    private static LocalDateTime normalizeUpdatedAt(LocalDateTime createdAt, LocalDateTime updatedAt) {
        return updatedAt != null && updatedAt.equals(createdAt) ? null : updatedAt;
    }
}
