package br.pucminas.graphtest.application.domain.gce.model;

import br.pucminas.graphtest.application.domain.gce.rules.GceStructureRules;
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
 * Agregado raiz que representa um Grafo de Causa e Efeito.
 */
public class Gce extends BaseEntity {

    private UUID projectId;
    private String name;
    private String description;
    private Boolean selected;

    private final Map<String, GceNode> nodes;
    private final List<GceEdge> edges;
    private final List<GceRestriction> restrictions;

    /**
     * Cria um novo agregado de GCE com seu estado inicial completo.
     *
     * @param id identificador persistido do grafo, quando existente
     * @param projectId identificador do projeto ao qual o grafo pertence
     * @param name nome do grafo
     * @param description descricao opcional do grafo
     * @param selected indica se o grafo esta marcado como selecionado
     * @param nodes colecao inicial de nos
     * @param edges colecao inicial de arestas
     * @param restrictions colecao inicial de restricoes
     */
    public Gce(
            UUID id,
            UUID projectId,
            String name,
            String description,
            boolean selected,
            Collection<GceNode> nodes,
            Collection<GceEdge> edges,
            Collection<GceRestriction> restrictions
    ) {
        this(id, projectId, name, description, selected, nodes, edges, restrictions, null, null);
    }

    public Gce(
            UUID id,
            UUID projectId,
            String name,
            String description,
            boolean selected,
            Collection<GceNode> nodes,
            Collection<GceEdge> edges,
            Collection<GceRestriction> restrictions,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.projectId = requireUuid(projectId, "projectId");
        this.name = requireText(name, "name");
        this.description = normalizeDescription(description);
        this.selected = selected;
        this.nodes = toNodeMap(nodes);
        this.edges = toList(edges, "edges");
        this.restrictions = toList(restrictions, "restrictions");

        validateAggregate();
    }

    private Map<String, GceNode> toNodeMap(Collection<GceNode> values) {
        Map<String, GceNode> map = new LinkedHashMap<>();
        if (values == null) {
            return map;
        }

        for (GceNode value : values) {
            if (value == null) {
            throw new IllegalArgumentException("A coleção do grafo não pode conter valores nulos.");
            }
            if (map.containsKey(value.getCode())) {
            throw new IllegalArgumentException("Há código de nó duplicado no grafo.");
            }
            map.put(value.getCode(), value);
        }
        return map;
    }

    private <T> List<T> toList(Collection<T> values, String field) {
        if (values == null) {
            return new ArrayList<>();
        }
        if (values.stream().anyMatch(item -> item == null)) {
            throw new IllegalArgumentException(displayFieldName(field) + " não pode conter valores nulos.");
        }
        return new ArrayList<>(values);
    }

    private UUID requireUuid(UUID value, String field) {
        if (value == null) {
            throw new IllegalArgumentException(displayFieldName(field) + " é obrigatório.");
        }
        return value;
    }

    private String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(displayFieldName(field) + " é obrigatório.");
        }
        return value.trim();
    }

    private String displayFieldName(String field) {
        return switch (field) {
            case "projectId" -> "O projeto";
            case "name" -> "O nome";
            case "edges" -> "As arestas";
            case "restrictions" -> "As restrições";
            default -> field;
        };
    }

    private String normalizeDescription(String value) {
        return value == null ? "" : value.trim();
    }

    void validateAggregate() {
        GceStructureRules.validate(this);
    }

    private GceNode requireNode(String nodeCode) {
        GceNode node = nodes.get(nodeCode);
        if (node == null) {
            throw new IllegalArgumentException("Nó não encontrado: " + nodeCode);
        }
        return node;
    }

    private GceEdge requireEdge(UUID edgeId) {
        return edges.stream()
                .filter(edge -> edgeId != null && edgeId.equals(edge.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Aresta inexistente: " + edgeId));
    }

    private void ensureNodeCodeAvailable(String code, String ignoredNodeCode) {
        boolean alreadyInUse = nodes.values().stream()
                .anyMatch(node -> node.getCode().equals(code) && !node.getCode().equals(ignoredNodeCode));

        if (alreadyInUse) {
            throw new IllegalArgumentException("Já existe um nó com o código informado: " + code);
        }
    }

    private void ensureEdgeSignatureAvailable(GceEdge edge, UUID ignoredEdgeId) {
        boolean alreadyInUse = edges.stream()
                .anyMatch(existingEdge -> existingEdge.sameSignature(edge)
                        && (ignoredEdgeId == null || !ignoredEdgeId.equals(existingEdge.getId())));

        if (alreadyInUse) {
            throw new IllegalArgumentException("Já existe aresta com a mesma origem, destino e tipo.");
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

    public boolean isSelected() {
        return selected;
    }

    public Collection<GceNode> getNodes() {
        return Collections.unmodifiableCollection(nodes.values());
    }

    public Collection<GceEdge> getEdges() {
        return Collections.unmodifiableList(edges);
    }

    public Collection<GceRestriction> getRestrictions() {
        return Collections.unmodifiableList(restrictions);
    }

    public Optional<GceNode> findNode(String nodeCode) {
        return Optional.ofNullable(nodes.get(nodeCode));
    }

    public Optional<GceEdge> findEdge(UUID edgeId) {
        return edges.stream()
                .filter(edge -> edgeId != null && edgeId.equals(edge.getId()))
                .findFirst();
    }

    public List<GceNode> getCauseNodes() {
        return nodes.values().stream().filter(GceNode::isCause).toList();
    }

    public List<GceNode> getEffectNodes() {
        return nodes.values().stream().filter(GceNode::isEffect).toList();
    }

    public List<GceNode> getOperatorNodes() {
        return nodes.values().stream().filter(GceNode::isOperator).toList();
    }

    public List<GceEdge> incomingEdges(String nodeCode) {
        return edges.stream()
                .filter(edge -> edge.targets(nodeCode))
                .toList();
    }

    public List<GceEdge> outgoingEdges(String nodeCode) {
        return edges.stream()
                .filter(edge -> edge.startsFrom(nodeCode))
                .toList();
    }

    public Map<String, Long> countNodeCodes() {
        return nodes.values().stream()
                .collect(Collectors.groupingBy(GceNode::getCode, Collectors.counting()));
    }

    public void updateDetails(String name, String description) {
        this.name = requireText(name, "name");
        this.description = normalizeDescription(description);
    }

    public void select() {
        this.selected = true;
    }

    public void unselect() {
        this.selected = false;
    }

    public void addNode(GceNode node) {
        if (node == null) {
            throw new IllegalArgumentException("O nó é obrigatório.");
        }

        ensureNodeCodeAvailable(node.getCode(), null);
        nodes.put(node.getCode(), node);
    }

    public void replaceNode(GceNode node) {
        if (node == null) {
            throw new IllegalArgumentException("O nó é obrigatório.");
        }
        GceNode previousNode = requireNode(node.getCode());

        ensureNodeCodeAvailable(node.getCode(), node.getCode());
        nodes.put(node.getCode(), node);

        try {
            validateAggregate();
        } catch (RuntimeException exception) {
            nodes.put(previousNode.getCode(), previousNode);
            throw exception;
        }
    }

    public void removeNode(String nodeCode) {
        String requiredNodeCode = requireText(nodeCode, "nodeCode");
        GceNode node = requireNode(requiredNodeCode);

        boolean hasAttachedEdges = edges.stream().anyMatch(edge -> edge.references(requiredNodeCode));
        if (hasAttachedEdges) {
            throw new IllegalArgumentException("Não é permitido remover o nó " + node.getCode() + " enquanto houver arestas ligadas a ele.");
        }

        boolean hasAttachedRestrictions = restrictions.stream().anyMatch(restriction -> restriction.references(requiredNodeCode));
        if (hasAttachedRestrictions) {
            throw new IllegalArgumentException("Não é permitido remover o nó " + node.getCode() + " enquanto houver restrições ligadas a ele.");
        }

        nodes.remove(requiredNodeCode);
    }

    public void addEdge(GceEdge edge) {
        if (edge == null) {
            throw new IllegalArgumentException("A aresta é obrigatória.");
        }

        requireNode(edge.getSourceNodeCode());
        requireNode(edge.getTargetNodeCode());
        ensureEdgeSignatureAvailable(edge, null);
        edges.add(edge);

        try {
            validateAggregate();
        } catch (RuntimeException exception) {
            edges.remove(edge);
            throw exception;
        }
    }

    public void replaceEdge(GceEdge edge) {
        if (edge == null || edge.getId() == null) {
            throw new IllegalArgumentException("A aresta com identificador é obrigatória.");
        }
        GceEdge previousEdge = requireEdge(edge.getId());

        requireNode(edge.getSourceNodeCode());
        requireNode(edge.getTargetNodeCode());
        ensureEdgeSignatureAvailable(edge, edge.getId());

        int index = edges.indexOf(previousEdge);
        edges.set(index, edge);

        try {
            validateAggregate();
        } catch (RuntimeException exception) {
            edges.set(index, previousEdge);
            throw exception;
        }
    }

}
