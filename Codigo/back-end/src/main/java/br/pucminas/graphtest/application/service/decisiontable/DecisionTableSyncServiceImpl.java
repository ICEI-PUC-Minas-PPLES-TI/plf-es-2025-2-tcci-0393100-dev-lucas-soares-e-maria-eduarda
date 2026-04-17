package br.pucminas.graphtest.application.service.decisiontable;

import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTable;
import br.pucminas.graphtest.application.domain.gce.model.GceEdge;
import br.pucminas.graphtest.application.domain.gce.model.GceRestriction;
import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.service.decisiontable.interfaces.DecisionTableSyncService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.Objects;

/**
 * Implementacao concreta do servico responsavel por verificar sincronizacao entre tabela de decisao e GCE.
 */
public class DecisionTableSyncServiceImpl implements DecisionTableSyncService {

    @Override
    public boolean isStale(DecisionTable decisionTable, Gce graph) {
        Objects.requireNonNull(decisionTable, "decisionTable e obrigatorio.");
        return graph == null
                || hasDifferentTimestamp(decisionTable, graph)
                || hasDifferentFingerprint(decisionTable, graph);
    }

    private boolean hasDifferentTimestamp(DecisionTable decisionTable, Gce graph) {
        return !Objects.equals(decisionTable.getSourceGceUpdatedAt(), effectiveGraphTimestamp(graph));
    }

    private boolean hasDifferentFingerprint(DecisionTable decisionTable, Gce graph) {
        return !buildFingerprint(graph).equals(decisionTable.getSourceFingerprint());
    }

    private LocalDateTime effectiveGraphTimestamp(Gce graph) {
        return graph.getUpdatedAt() != null ? graph.getUpdatedAt() : graph.getCreatedAt();
    }

    private String buildFingerprint(Gce graph) {
        StringBuilder builder = new StringBuilder();
        appendGraphMetadata(graph, builder);
        appendNodes(graph, builder);
        appendEdges(graph, builder);
        appendRestrictions(graph, builder);
        return hash(builder.toString());
    }

    private void appendGraphMetadata(Gce graph, StringBuilder builder) {
        builder.append(graph.getProjectId()).append('|')
                .append(graph.getName()).append('|')
                .append(graph.getDescription()).append('|');
    }

    private void appendNodes(Gce graph, StringBuilder builder) {
        graph.getNodes().stream()
                .sorted(Comparator.comparing(node -> node.getCode()))
                .forEach(node -> appendNode(builder, node));
    }

    private void appendNode(StringBuilder builder, br.pucminas.graphtest.application.domain.gce.model.GceNode node) {
        builder.append("N:")
                .append(node.getCode()).append(':')
                .append(node.getLabel()).append(':')
                .append(node.getType()).append(':')
                .append(node.getOperatorType())
                .append('|');
    }

    private void appendEdges(Gce graph, StringBuilder builder) {
        graph.getEdges().stream()
                .sorted(Comparator.comparing(GceEdge::getSourceNodeCode)
                        .thenComparing(GceEdge::getTargetNodeCode)
                        .thenComparing(edge -> edge.getType().name()))
                .forEach(edge -> appendEdge(builder, edge));
    }

    private void appendEdge(StringBuilder builder, GceEdge edge) {
        builder.append("E:")
                .append(edge.getSourceNodeCode()).append(':')
                .append(edge.getTargetNodeCode()).append(':')
                .append(edge.getType())
                .append('|');
    }

    private void appendRestrictions(Gce graph, StringBuilder builder) {
        graph.getRestrictions().stream()
                .sorted(Comparator.comparing((GceRestriction restriction) -> restriction.getType().name())
                        .thenComparing(restriction -> String.join(",", restriction.getNodeCodes())))
                .forEach(restriction -> appendRestriction(builder, restriction));
    }

    private void appendRestriction(StringBuilder builder, GceRestriction restriction) {
        builder.append("R:")
                .append(restriction.getType()).append(':')
                .append(String.join(",", restriction.getNodeCodes()))
                .append('|');
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Nao foi possivel calcular o fingerprint da tabela de decisao.", exception);
        }
    }
}
