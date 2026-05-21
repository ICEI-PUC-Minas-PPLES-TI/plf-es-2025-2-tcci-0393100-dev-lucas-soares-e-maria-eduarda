package br.pucminas.graphtest.application.port.input.gfc.records;

import br.pucminas.graphtest.application.domain.gfc.model.Gfc;

import java.util.UUID;

/**
 * Saida resumida de um GFC para listagem.
 */
public record GfcSummaryOutput(
        UUID id,
        UUID projectId,
        UUID sourceFileId,
        String methodSignature,
        String name,
        String description,
        String language
) {

    public static GfcSummaryOutput from(Gfc gfc) {
        return new GfcSummaryOutput(
                gfc.getId(),
                gfc.getProjectId(),
                gfc.getSourceFileId(),
                gfc.getMethodSignature(),
                gfc.getName(),
                gfc.getDescription(),
                gfc.getLanguage()
        );
    }
}
