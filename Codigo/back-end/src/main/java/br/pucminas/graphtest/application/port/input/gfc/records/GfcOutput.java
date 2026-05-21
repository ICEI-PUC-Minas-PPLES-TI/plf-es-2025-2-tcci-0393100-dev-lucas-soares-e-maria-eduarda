package br.pucminas.graphtest.application.port.input.gfc.records;

import br.pucminas.graphtest.application.domain.gfc.model.Gfc;
import br.pucminas.graphtest.application.domain.gfc.model.GfcNode;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Saida com a representacao completa de um GFC.
 */
public record GfcOutput(
        UUID id,
        UUID projectId,
        UUID sourceFileId,
        String methodSignature,
        String name,
        String description,
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
                graph.getSourceFileId(),
                graph.getMethodSignature(),
                graph.getName(),
                graph.getDescription(),
                graph.getLanguage(),
                graph.getNodes().stream()
                        .sorted(Comparator.comparingInt(GfcOutput::nodeSortIndex))
                        .map(GfcNodeOutput::from)
                        .toList(),
                graph.getEdges().stream().map(GfcEdgeOutput::from).toList()
        );
    }

    private static int nodeSortIndex(GfcNode node) {
        if ("N_END".equals(node.getCode())) {
            return Integer.MAX_VALUE;
        }
        if (node.getCode().startsWith("N")) {
            try {
                return Integer.parseInt(node.getCode().substring(1));
            } catch (NumberFormatException ignored) {
                return Integer.MAX_VALUE - 1;
            }
        }
        return Integer.MAX_VALUE - 1;
    }
}
