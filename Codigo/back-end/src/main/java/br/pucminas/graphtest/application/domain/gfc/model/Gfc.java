package br.pucminas.graphtest.application.domain.gfc.model;

import br.pucminas.graphtest.application.domain.shared.model.BaseEntity;
import br.pucminas.graphtest.application.exception.InvalidGfcModelException;

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

import static br.pucminas.graphtest.application.domain.gfc.rules.GfcDomainRules.normalizeJavaLanguage;
import static br.pucminas.graphtest.application.domain.gfc.rules.GfcDomainRules.normalizeOptionalText;
import static br.pucminas.graphtest.application.domain.gfc.rules.GfcDomainRules.requireText;
import static br.pucminas.graphtest.application.domain.gfc.rules.GfcDomainRules.requireUuid;

/**
 * Agregado raiz que representa um Grafo de Fluxo de Controle.
 */
public class Gfc extends BaseEntity {

    private UUID projectId;
    private UUID sourceFileId;
    private String methodSignature;
    private String name;
    private String description;
    private String language;
    private final Map<String, GfcNode> nodes;
    private final List<GfcEdge> edges;

    public static Gfc preview(UUID id,
                              UUID projectId,
                              String methodSignature,
                              String name,
                              String description,
                              String language,
                              Collection<GfcNode> nodes,
                              Collection<GfcEdge> edges) {
        return new Gfc(id, projectId, null, methodSignature, name, description, language, nodes, edges, LocalDateTime.now(), null, false);
    }

    public static Gfc persisted(UUID id,
                                UUID projectId,
                                UUID sourceFileId,
                                String methodSignature,
                                String name,
                                String description,
                                String language,
                                Collection<GfcNode> nodes,
                                Collection<GfcEdge> edges) {
        return new Gfc(id, projectId, sourceFileId, methodSignature, name, description, language, nodes, edges, LocalDateTime.now(), null, true);
    }

    public static Gfc reconstitute(UUID id,
                                   UUID projectId,
                                   UUID sourceFileId,
                                   String methodSignature,
                                   String name,
                                   String description,
                                   String language,
                                   Collection<GfcNode> nodes,
                                   Collection<GfcEdge> edges,
                                   LocalDateTime createdAt,
                                   LocalDateTime updatedAt) {
        return new Gfc(id, projectId, sourceFileId, methodSignature, name, description, language, nodes, edges, createdAt, updatedAt, true);
    }

    private Gfc(UUID id,
                UUID projectId,
                UUID sourceFileId,
                String methodSignature,
                String name,
                String description,
                String language,
                Collection<GfcNode> nodes,
                Collection<GfcEdge> edges,
                LocalDateTime createdAt,
                LocalDateTime updatedAt,
                boolean requirePersistedReferences) {
        this.id = id;
        this.createdAt = requireCreatedAt(createdAt);
        this.updatedAt = updatedAt;
        this.projectId = requireUuid(projectId, "O projeto");
        this.sourceFileId = requirePersistedReferences ? requireUuid(sourceFileId, "O arquivo-fonte") : sourceFileId;
        this.methodSignature = requirePersistedReferences
                ? requireText(methodSignature, "A assinatura do metodo")
                : normalizeOptionalText(methodSignature);
        this.name = requireText(name, "O nome");
        this.description = normalizeOptionalText(description);
        this.language = normalizeJavaLanguage(language);
        this.nodes = toNodeMap(nodes);
        this.edges = toList(edges, "As arestas");

        validateAggregate();
    }

    private LocalDateTime requireCreatedAt(LocalDateTime value) {
        if (value == null) {
            throw new InvalidGfcModelException("A data de criacao do GFC e obrigatoria.");
        }
        return value;
    }

    private Map<String, GfcNode> toNodeMap(Collection<GfcNode> values) {
        Map<String, GfcNode> map = new LinkedHashMap<>();
        if (values == null) {
            return map;
        }

        for (GfcNode value : values) {
            if (value == null) {
                throw new InvalidGfcModelException("A colecao de nos nao pode conter valores nulos.");
            }
            if (map.containsKey(value.getCode())) {
                throw new InvalidGfcModelException("Ha codigo de no duplicado no GFC.");
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
            throw new InvalidGfcModelException(field + " nao pode conter valores nulos.");
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
            throw new InvalidGfcModelException("No nao encontrado: " + nodeCode);
        }
        return node;
    }

    private void ensureUniqueEdgeSignatures() {
        List<String> signatures = edges.stream()
                .map(edge -> edge.getSourceNodeCode() + ":" + edge.getTargetNodeCode() + ":" + edge.getType() + ":" + edge.getLabel())
                .toList();
        long uniqueSignatures = signatures.stream().distinct().count();
        if (uniqueSignatures != signatures.size()) {
            throw new InvalidGfcModelException("Ha aresta duplicada no GFC.");
        }
    }

    private void ensureNodeCodeAvailable(String code) {
        if (nodes.containsKey(code)) {
            throw new InvalidGfcModelException("Ja existe um no com o codigo informado: " + code);
        }
    }

    private void ensureEdgeSignatureAvailable(GfcEdge edge) {
        boolean alreadyInUse = edges.stream().anyMatch(existingEdge -> existingEdge.sameSignature(edge));
        if (alreadyInUse) {
            throw new InvalidGfcModelException("Ja existe aresta com a mesma origem, destino e tipo.");
        }
    }

    public UUID getProjectId() {
        return projectId;
    }

    public UUID getSourceFileId() {
        return sourceFileId;
    }

    public String getMethodSignature() {
        return methodSignature;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
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
        this.name = requireText(name, "O nome");
        this.description = normalizeOptionalText(description);
    }

    public void addNode(GfcNode node) {
        if (node == null) {
            throw new InvalidGfcModelException("O no e obrigatorio.");
        }
        ensureNodeCodeAvailable(node.getCode());
        nodes.put(node.getCode(), node);
    }

    public void addEdge(GfcEdge edge) {
        if (edge == null) {
            throw new InvalidGfcModelException("A aresta e obrigatoria.");
        }
        requireNode(edge.getSourceNodeCode());
        requireNode(edge.getTargetNodeCode());
        ensureEdgeSignatureAvailable(edge);
        edges.add(edge);
    }
}
