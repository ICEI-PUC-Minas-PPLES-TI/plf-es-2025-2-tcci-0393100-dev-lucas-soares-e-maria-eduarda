package br.pucminas.graphtest.application.domain;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Gce extends BaseEntity{

    private UUID projectId;
    private String name;
    private String description;
    private Boolean selected;

    private final Map<UUID, GceNode> nodes;
    private final Map<UUID, GceEdge> edges;
    private final Map<UUID, GceRestriction> restrictions;

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
        this.id = id;
        this.projectId = projectId;
        this.name = name;
        this.description = description == null ? "" : description.trim();
        this.selected = selected;
        this.nodes = toMap(nodes, GceNode::getId);
        this.edges = toMap(edges, GceEdge::getId);
        this.restrictions = toMap(restrictions, GceRestriction::getId);
    }

    private <K, V> Map<K, V> toMap(Collection<V> values, Function<V, K> idFn) {
        Map<K, V> map = new LinkedHashMap<>();

        if (values == null) {
            return map;
        }

        for (V value : values) {
            K key = idFn.apply(value);
            if (map.containsKey(key)) {
                throw new IllegalArgumentException("Identificador duplicado no agregado.");
            }
            map.put(key, value);
        }
        return map;
    }

    private String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " é obrigatório.");
        }
        return value.trim();
    }

    public UUID getId() {
        return id;
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
        return Collections.unmodifiableCollection(edges.values());
    }

    public Collection<GceRestriction> getRestrictions() {
        return Collections.unmodifiableCollection(restrictions.values());
    }

    public Optional<GceNode> findNode(UUID nodeId) {
        return Optional.ofNullable(nodes.get(nodeId));
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

    public List<GceEdge> incomingEdges(UUID nodeId) {
        return edges.values().stream()
                .filter(edge -> edge.getTargetNodeId().equals(nodeId))
                .toList();
    }

    public List<GceEdge> outgoingEdges(UUID nodeId) {
        return edges.values().stream()
                .filter(edge -> edge.getSourceNodeId().equals(nodeId))
                .toList();
    }

    public Map<String, Long> countNodeCodes() {
        return nodes.values().stream()
                .collect(Collectors.groupingBy(GceNode::getCode, Collectors.counting()));
    }
}
