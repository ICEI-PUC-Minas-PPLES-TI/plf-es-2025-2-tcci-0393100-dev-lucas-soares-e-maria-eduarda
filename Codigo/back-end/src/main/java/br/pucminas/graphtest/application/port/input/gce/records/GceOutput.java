package br.pucminas.graphtest.application.port.input.gce.records;

import br.pucminas.graphtest.application.domain.Gce;

import java.util.List;
import java.util.UUID;

/**
 * Saida com a representacao completa de um GCE.
 */
public record GceOutput(
        UUID id,
        UUID projectId,
        String name,
        String description,
        boolean selected,
        List<GceNodeOutput> nodes,
        List<GceEdgeOutput> edges,
        List<GceRestrictionOutput> restrictions
) {

    /**
     * Converte um agregado de dominio em sua representacao de saida.
     *
     * @param graph agregado do dominio
     * @return saida correspondente
     */
    public static GceOutput from(Gce graph) {
        return new GceOutput(
                graph.getId(),
                graph.getProjectId(),
                graph.getName(),
                graph.getDescription(),
                graph.isSelected(),
                graph.getNodes().stream().map(GceNodeOutput::from).toList(),
                graph.getEdges().stream().map(GceEdgeOutput::from).toList(),
                graph.getRestrictions().stream().map(GceRestrictionOutput::from).toList()
        );
    }
}
