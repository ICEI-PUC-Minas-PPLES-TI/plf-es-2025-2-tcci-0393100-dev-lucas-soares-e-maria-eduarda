package br.pucminas.graphtest.application.service.gce;

import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.domain.gce.model.GceEdge;
import br.pucminas.graphtest.application.domain.gce.model.GceNode;
import br.pucminas.graphtest.application.domain.gce.model.GceRestriction;
import br.pucminas.graphtest.application.domain.gce.enums.GceEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.GceNodeTypeEnum;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.exception.InvalidGceModelException;
import br.pucminas.graphtest.application.port.input.gce.records.GceEdgeInput;
import br.pucminas.graphtest.application.port.input.gce.records.GceNodeInput;
import br.pucminas.graphtest.application.port.input.gce.records.GceRestrictionInput;
import br.pucminas.graphtest.application.port.input.gce.records.ValidationGceOutput;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.service.gce.interfaces.GceMutationService;
import br.pucminas.graphtest.application.service.gce.interfaces.GceValidationResultService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Implementacao concreta do servico de apoio para mutacoes do GCE.
 */
public class GceMutationServiceImpl implements GceMutationService {

    @Override
    public Gce loadAuthorizedGraph(UUID id,
                                   GceRepositoryPort gceRepository,
                                   ProjectAccessService projectAccessService) {
        Gce graph = gceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("GCE nao encontrado"));
        projectAccessService.findAuthorizedProject(graph.getProjectId());
        return graph;
    }

    @Override
    public void validateAndThrow(Gce graph, GceValidationResultService validationService) {
        ValidationGceOutput validation = validationService.validate(graph);
        if (!validation.valid()) {
            throw new InvalidGceModelException("GCE invalido: " + validation.errors());
        }
    }

    @Override
    public Collection<GceNode> toNodes(List<GceNodeInput> nodes) {
        if (nodes == null) {
            return List.of();
        }

        return nodes.stream()
                .map(this::toNode)
                .toList();
    }

    @Override
    public Collection<GceEdge> toEdges(List<GceNodeInput> nodes, List<GceEdgeInput> explicitEdges) {
        List<GceEdge> edges = new ArrayList<>();
        if (nodes != null) {
            for (GceNodeInput node : nodes) {
                edges.addAll(buildAutomaticEdges(node));
            }
        }

        if (explicitEdges != null) {
            explicitEdges.stream()
                    .map(edge -> new GceEdge(
                            UUID.randomUUID(),
                            edge.sourceNodeCode(),
                            edge.targetNodeCode(),
                            edge.type()
                    ))
                    .forEach(edges::add);
        }

        return edges;
    }

    @Override
    public Collection<GceRestriction> toRestrictions(List<GceRestrictionInput> restrictions) {
        if (restrictions == null) {
            return List.of();
        }

        return restrictions.stream()
                .map(restriction -> new GceRestriction(
                        null,
                        restriction.type(),
                        restriction.nodeCodes()
                ))
                .toList();
    }

    @Override
    public void addNodeWithAutomaticEdges(Gce graph, GceNodeInput nodeInput) {
        graph.addNode(toNode(nodeInput));
        for (GceEdge edge : buildAutomaticEdges(nodeInput)) {
            graph.addEdge(edge);
        }
    }

    @Override
    public void refreshOperatorLabels(Gce graph) {
        Map<String, String> expressionByNodeCode = new HashMap<>();

        for (GceNode operatorNode : graph.getOperatorNodes()) {
            String generatedLabel = buildNodeExpression(graph, operatorNode.getCode(), expressionByNodeCode, new HashSet<>());
            graph.replaceNode(new GceNode(
                    operatorNode.getId(),
                    operatorNode.getCode(),
                    generatedLabel,
                    operatorNode.getType(),
                    operatorNode.getOperatorType()
            ));
        }
    }

    private GceNode toNode(GceNodeInput node) {
        Objects.requireNonNull(node, "node e obrigatorio.");
        validateNodeConnectionContract(node);
        return new GceNode(null, node.code(), resolveInitialLabel(node), node.type(), node.operatorType());
    }

    private String resolveInitialLabel(GceNodeInput node) {
        if (node.type() == GceNodeTypeEnum.OPERATOR) {
            return node.code();
        }
        return node.label();
    }

    private List<GceEdge> buildAutomaticEdges(GceNodeInput node) {
        validateNodeConnectionContract(node);

        List<GceEdge> edges = new ArrayList<>();
        for (String sourceNodeCode : normalizeNodeCodes(node.sourceNodeCodes())) {
            edges.add(new GceEdge(UUID.randomUUID(), sourceNodeCode, node.code(), GceEdgeTypeEnum.IDENTITY));
        }
        for (String targetNodeCode : normalizeNodeCodes(node.targetNodeCodes())) {
            edges.add(new GceEdge(UUID.randomUUID(), node.code(), targetNodeCode, GceEdgeTypeEnum.IDENTITY));
        }
        return edges;
    }

    private void validateNodeConnectionContract(GceNodeInput node) {
        List<String> sourceNodeCodes = normalizeNodeCodes(node.sourceNodeCodes());
        List<String> targetNodeCodes = normalizeNodeCodes(node.targetNodeCodes());
        boolean hasAutomaticConnections = !sourceNodeCodes.isEmpty() || !targetNodeCodes.isEmpty();

        if (!hasAutomaticConnections) {
            return;
        }

        if (node.type() == GceNodeTypeEnum.CAUSE) {
            if (!sourceNodeCodes.isEmpty()) {
                throw new IllegalArgumentException("No CAUSE nao pode receber sourceNodeCodes.");
            }
            return;
        }

        if (node.type() == GceNodeTypeEnum.EFFECT) {
            if (sourceNodeCodes.size() != 1) {
                throw new IllegalArgumentException("No EFFECT deve informar exatamente 1 sourceNodeCode.");
            }
            if (!targetNodeCodes.isEmpty()) {
                throw new IllegalArgumentException("No EFFECT nao pode informar targetNodeCodes.");
            }
            return;
        }

        if (sourceNodeCodes.size() != 2) {
            throw new IllegalArgumentException("No OPERATOR deve informar exatamente 2 sourceNodeCodes.");
        }
        if (targetNodeCodes.size() != 1) {
            throw new IllegalArgumentException("No OPERATOR deve informar exatamente 1 targetNodeCode.");
        }
    }

    private List<String> normalizeNodeCodes(List<String> nodeCodes) {
        if (nodeCodes == null) {
            return List.of();
        }

        return nodeCodes.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(code -> !code.isBlank())
                .toList();
    }

    private String buildNodeExpression(Gce graph,
                                       String nodeCode,
                                       Map<String, String> expressionByNodeCode,
                                       Set<String> visiting) {
        String cachedExpression = expressionByNodeCode.get(nodeCode);
        if (cachedExpression != null) {
            return cachedExpression;
        }

        if (!visiting.add(nodeCode)) {
            throw new IllegalArgumentException("Nao foi possivel gerar a expressao do operador devido a ciclo envolvendo o no " + nodeCode);
        }

        GceNode node = graph.findNode(nodeCode)
                .orElseThrow(() -> new IllegalArgumentException("No inexistente: " + nodeCode));

        String expression;
        if (!node.isOperator()) {
            expression = node.getCode();
        } else {
            List<String> operands = graph.incomingEdges(nodeCode).stream()
                    .sorted(Comparator.comparing(GceEdge::getSourceNodeCode).thenComparing(edge -> edge.getType().name()))
                    .map(edge -> formatOperandExpression(
                            buildNodeExpression(graph, edge.getSourceNodeCode(), expressionByNodeCode, visiting),
                            edge
                    ))
                    .toList();

            String connector = node.getOperatorType().name();
            expression = operands.size() <= 1
                    ? operands.stream().findFirst().orElse(node.getCode())
                    : "(" + String.join(" " + connector + " ", operands) + ")";
        }

        visiting.remove(nodeCode);
        expressionByNodeCode.put(nodeCode, expression);
        return expression;
    }

    private String formatOperandExpression(String operandExpression, GceEdge edge) {
        if (edge.isNegated()) {
            return "NOT (" + operandExpression + ")";
        }

        return operandExpression;
    }
}
