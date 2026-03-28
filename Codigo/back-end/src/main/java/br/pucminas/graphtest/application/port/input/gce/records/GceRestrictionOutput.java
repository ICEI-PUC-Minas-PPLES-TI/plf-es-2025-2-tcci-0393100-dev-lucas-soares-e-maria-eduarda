package br.pucminas.graphtest.application.port.input.gce.records;

import br.pucminas.graphtest.application.domain.GceRestriction;
import br.pucminas.graphtest.application.domain.enums.RestrictionTypeEnum;

import java.util.List;

/**
 * Saida de uma restricao do GCE.
 */
public record GceRestrictionOutput(
        Long id,
        RestrictionTypeEnum type,
        List<String> nodeCodes
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
                restriction.getNodeCodes()
        );
    }
}
