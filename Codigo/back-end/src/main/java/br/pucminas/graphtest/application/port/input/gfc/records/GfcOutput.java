package br.pucminas.graphtest.application.port.input.gfc.records;

import br.pucminas.graphtest.application.domain.gfc.model.Gfc;

import java.util.List;
import java.util.UUID;

/**
 * Saida com a representacao completa de um GFC.
 */
public record GfcOutput(
        UUID id,
        UUID projectId,
        String name,
        String description,
        String sourceCode,
        String language,
        List<GfcNodeOutput> nodes,
        List<GfcEdgeOutput> edges
) {

    /**
     * Converte um agregado de dominio em sua representacao de saida.
     *
     * @param graph agregado do dominio
     * @return saida correspondente
     */
    public static GfcOutput from(Gfc graph) {
        return new GfcOutput(
                graph.getId(),
                graph.getProjectId(),
                graph.getName(),
                graph.getDescription(),
                graph.getSourceCode(),
                graph.getLanguage(),
                graph.getNodes().stream().map(GfcNodeOutput::from).toList(),
                graph.getEdges().stream().map(GfcEdgeOutput::from).toList()
        );
    }
}
