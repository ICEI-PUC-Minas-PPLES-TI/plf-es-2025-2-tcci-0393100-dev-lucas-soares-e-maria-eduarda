package br.pucminas.graphtest.application.service.decisiontable;

import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.domain.gce.model.GceEdge;
import br.pucminas.graphtest.application.domain.gce.model.GceNode;
import br.pucminas.graphtest.application.domain.gce.model.GceRestriction;
import br.pucminas.graphtest.application.port.output.repositories.DecisionTableRepositoryPort;
import br.pucminas.graphtest.application.service.decisiontable.interfaces.DecisionTableSyncStatusUpdateService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.Objects;
import java.util.UUID;

/**
 * Implementacao concreta do servico responsavel por persistir a marcacao de desatualizacao
 * da tabela de decisao quando o GCE de origem muda semanticamente.
 */
public class DecisionTableSyncStatusUpdateServiceImpl implements DecisionTableSyncStatusUpdateService {

    private final DecisionTableRepositoryPort decisionTableRepository;

    public DecisionTableSyncStatusUpdateServiceImpl(DecisionTableRepositoryPort decisionTableRepository) {
        this.decisionTableRepository = decisionTableRepository;
    }

    @Override
    public void markDecisionTableAsStaleByGceId(UUID gceId) {
        decisionTableRepository.findByGceId(gceId).ifPresent(decisionTable -> {
            if (!decisionTable.isStale()) {
                decisionTable.markAsStale();
                decisionTableRepository.save(decisionTable);
            }
        });
    }

    @Override
    public boolean hasDecisionTableRelevantChanges(Gce previousGraph, Gce currentGraph) {
        Objects.requireNonNull(previousGraph, "previousGraph e obrigatorio.");
        Objects.requireNonNull(currentGraph, "currentGraph e obrigatorio.");
        return !buildRelevantFingerprint(previousGraph).equals(buildRelevantFingerprint(currentGraph));
    }

    private String buildRelevantFingerprint(Gce graph) {
        StringBuilder builder = new StringBuilder();
        appendGraphIdentity(graph, builder);
        appendNodes(graph, builder);
        appendEdges(graph, builder);
        appendRestrictions(graph, builder);
        return hash(builder.toString());
    }

    private void appendGraphIdentity(Gce graph, StringBuilder builder) {
        builder.append(graph.getProjectId()).append('|');
    }

    private void appendNodes(Gce graph, StringBuilder builder) {
        graph.getNodes().stream()
                .sorted(Comparator.comparing(GceNode::getCode))
                .forEach(node -> appendNode(builder, node));
    }

    private void appendNode(StringBuilder builder, GceNode node) {
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
                        .thenComparing(this::normalizedRestrictionNodeCodes))
                .forEach(restriction -> appendRestriction(builder, restriction));
    }

    private void appendRestriction(StringBuilder builder, GceRestriction restriction) {
        builder.append("R:")
                .append(restriction.getType()).append(':')
                .append(normalizedRestrictionNodeCodes(restriction))
                .append('|');
    }

    private String normalizedRestrictionNodeCodes(GceRestriction restriction) {
        return restriction.getNodeCodes().stream()
                .sorted()
                .collect(java.util.stream.Collectors.joining(","));
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Nao foi possivel calcular o fingerprint relevante do GCE.", exception);
        }
    }
}
