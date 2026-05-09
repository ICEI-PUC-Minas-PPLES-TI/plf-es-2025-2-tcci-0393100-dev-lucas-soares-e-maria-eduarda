package br.pucminas.graphtest.application.domain.gfc.model;

import br.pucminas.graphtest.application.domain.shared.model.BaseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Agregado raiz que representa um Grafo de Fluxo de Controle.
 */
public class Gfc extends BaseEntity {

    public static final String JAVA_LANGUAGE = "Java";

    private UUID projectId;
    private String name;
    private String description;
    private String sourceCode;
    private String language;
    private final Map<String, GfcNode> nodes;
    private final List<GfcEdge> edges;

    public Gfc(UUID id,
               UUID projectId,
               String name,
               String description,
               String sourceCode,
               String language,
               Collection<GfcNode> nodes,
               Collection<GfcEdge> edges) {
        this(id, projectId, name, description, sourceCode, language, nodes, edges, null, null);
    }

    public Gfc(UUID id,
               UUID projectId,
               String name,
               String description,
               String sourceCode,
               String language,
               Collection<GfcNode> nodes,
               Collection<GfcEdge> edges,
               LocalDateTime createdAt,
               LocalDateTime updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.projectId = requireUuid(projectId, "projectId");
        this.name = requireText(name, "name");
        this.description = normalizeDescription(description);
        this.sourceCode = normalizeSourceCode(sourceCode);
        this.language = normalizeLanguage(language);
        this.nodes = toNodeMap(nodes);
        this.edges = toList(edges, "edges");

        validateAggregate();
    }

    private UUID requireUuid(UUID value, String field) {
        if (value == null) {
            throw new IllegalArgumentException(displayFieldName(field) + " e obrigatorio.");
        }
        return value;
    }

    private String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(displayFieldName(field) + " e obrigatorio.");
        }
        return value.trim();
    }

    private String displayFieldName(String field) {
        return switch (field) {
            case "projectId" -> "O projeto";
            case "name" -> "O nome";
            case "language" -> "A linguagem";
            case "edges" -> "As arestas";
            default -> field;
        };
    }

    private String normalizeDescription(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeSourceCode(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeLanguage(String value) {
        String normalized = requireText(value, "language");
        if (!JAVA_LANGUAGE.equalsIgnoreCase(normalized)) {
            throw new IllegalArgumentException("A linguagem suportada para GFC e Java.");
        }
        return JAVA_LANGUAGE;
    }

    private Map<String, GfcNode> toNodeMap(Collection<GfcNode> values) {
        Map<String, GfcNode> map = new LinkedHashMap<>();
        if (values == null) {
            return map;
        }

        for (GfcNode value : values) {
            if (value == null) {
                throw new IllegalArgumentException("A colecao de nos nao pode conter valores nulos.");
            }
            if (map.containsKey(value.getCode())) {
                throw new IllegalArgumentException("Ha codigo de no duplicado no GFC.");
            }
            map.put(value.getCode(), value);
        }
        return map;
    }

    private List<GfcEdge> toList(Collection<GfcEdge> values, String field) {
        if (values == null) {
            return new ArrayList<>();
        }
        if (values.stream().anyMatch(item -> item == null)) {
            throw new IllegalArgumentException(displayFieldName(field) + " nao pode conter valores nulos.");
        }
        return new ArrayList<>(values);
    }

    private void validateAggregate() {
        for (GfcEdge edge : edges) {
            requireNode(edge.getSourceNodeCode());
            requireNode(edge.getTargetNodeCode());
        }
        ensureUniqueEdgeSignatures();
    }

    private GfcNode requireNode(String nodeCode) {
        GfcNode node = nodes.get(nodeCode);
        if (node == null) {
            throw new IllegalArgumentException("No nao encontrado: " + nodeCode);
        }
        return node;
    }

    private void ensureUniqueEdgeSignatures() {
        List<String> signatures = edges.stream()
                .map(edge -> edge.getSourceNodeCode() + ":" + edge.getTargetNodeCode() + ":" + edge.getType())
                .toList();
        long uniqueSignatures = signatures.stream().distinct().count();
        if (uniqueSignatures != signatures.size()) {
            throw new IllegalArgumentException("Ha aresta duplicada no GFC.");
        }
    }

    private void ensureNodeCodeAvailable(String code) {
        if (nodes.containsKey(code)) {
            throw new IllegalArgumentException("Ja existe um no com o codigo informado: " + code);
        }
    }

    private void ensureEdgeSignatureAvailable(GfcEdge edge) {
        boolean alreadyInUse = edges.stream().anyMatch(existingEdge -> existingEdge.sameSignature(edge));
        if (alreadyInUse) {
            throw new IllegalArgumentException("Ja existe aresta com a mesma origem, destino e tipo.");
        }
    }

    public UUID getProjectId() {
        return projectId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public String getLanguage() {
        return language;
    }

    public Collection<GfcNode> getNodes() {
        return Collections.unmodifiableCollection(nodes.values());
    }

    public Collection<GfcEdge> getEdges() {
        return Collections.unmodifiableList(edges);
    }

    public Optional<GfcNode> findNode(String nodeCode) {
        return Optional.ofNullable(nodes.get(nodeCode));
    }

    public List<GfcEdge> incomingEdges(String nodeCode) {
        return edges.stream()
                .filter(edge -> edge.targets(nodeCode))
                .toList();
    }

    public List<GfcEdge> outgoingEdges(String nodeCode) {
        return edges.stream()
                .filter(edge -> edge.startsFrom(nodeCode))
                .toList();
    }

    public Map<String, Long> countNodeCodes() {
        return nodes.values().stream()
                .collect(Collectors.groupingBy(GfcNode::getCode, Collectors.counting()));
    }

    public void updateDetails(String name, String description) {
        this.name = requireText(name, "name");
        this.description = normalizeDescription(description);
    }

    public void addNode(GfcNode node) {
        if (node == null) {
            throw new IllegalArgumentException("O no e obrigatorio.");
        }
        ensureNodeCodeAvailable(node.getCode());
        nodes.put(node.getCode(), node);
    }

    public void addEdge(GfcEdge edge) {
        if (edge == null) {
            throw new IllegalArgumentException("A aresta e obrigatoria.");
        }
        requireNode(edge.getSourceNodeCode());
        requireNode(edge.getTargetNodeCode());
        ensureEdgeSignatureAvailable(edge);
        edges.add(edge);
    }
}
