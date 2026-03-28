package br.pucminas.graphtest.application.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Agregado raiz que representa um Grafo de Causa e Efeito.
 *
 * <p>O agregado concentra a estrutura principal do modelo, incluindo seus nos,
 * arestas e restricoes, alem das operacoes de manutencao coerente desse estado.</p>
 */
public class Gce extends BaseEntity {

    private UUID projectId;
    private String name;
    private String description;
    private Boolean selected;

    private final Map<UUID, GceNode> nodes;
    private final Map<UUID, GceEdge> edges;
    private final Map<UUID, GceRestriction> restrictions;

    /**
     * Cria um novo agregado de GCE com seu estado inicial completo.
     *
     * @param id identificador do grafo
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
        this.id = requireUuid(id, "id");
        this.projectId = requireUuid(projectId, "projectId");
        this.name = requireText(name, "name");
        this.description = normalizeDescription(description);
        this.selected = selected;
        this.nodes = toMap(nodes, GceNode::getId);
        this.edges = toMap(edges, GceEdge::getId);
        this.restrictions = toMap(restrictions, GceRestriction::getId);

        validateAggregate();
    }

    /**
     * Converte uma colecao de elementos do agregado em um mapa indexado por identificador.
     *
     * @param values elementos a serem indexados
     * @param idFn funcao usada para extrair a chave de cada elemento
     * @return mapa indexado pelos identificadores extraidos
     * @param <K> tipo da chave do mapa
     * @param <V> tipo do valor armazenado
     */
    private <K, V> Map<K, V> toMap(Collection<V> values, Function<V, K> idFn) {
        Map<K, V> map = new LinkedHashMap<>();

        if (values == null) {
            return map;
        }

        for (V value : values) {
            if (value == null) {
                throw new IllegalArgumentException("Colecao do agregado nao pode conter valores nulos.");
            }

            K key = idFn.apply(value);
            if (key == null) {
                throw new IllegalArgumentException("Entidade do agregado deve possuir identificador.");
            }
            if (map.containsKey(key)) {
                throw new IllegalArgumentException("Identificador duplicado no agregado.");
            }
            map.put(key, value);
        }
        return map;
    }

    /**
     * Garante que um UUID obrigatorio foi informado.
     *
     * @param value valor a ser validado
     * @param field nome do campo para composicao da mensagem
     * @return o mesmo UUID informado quando valido
     */
    private UUID requireUuid(UUID value, String field) {
        if (value == null) {
            throw new IllegalArgumentException(field + " e obrigatorio.");
        }
        return value;
    }

    /**
     * Garante que um texto obrigatorio foi informado.
     *
     * @param value valor textual a ser validado
     * @param field nome do campo para composicao da mensagem
     * @return texto normalizado sem espacos excedentes
     */
    private String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " e obrigatorio.");
        }
        return value.trim();
    }

    /**
     * Normaliza a descricao opcional do grafo.
     *
     * @param value descricao recebida
     * @return descricao normalizada ou string vazia quando ausente
     */
    private String normalizeDescription(String value) {
        return value == null ? "" : value.trim();
    }

    /**
     * Reexecuta a validacao estrutural interna do agregado.
     */
    void validateAggregate() {
        GceStructureRules.validate(this);
    }

    /**
     * Localiza um no existente no agregado.
     *
     * @param nodeId identificador do no esperado
     * @return no encontrado
     */
    private GceNode requireNode(UUID nodeId) {
        GceNode node = nodes.get(nodeId);
        if (node == null) {
            throw new IllegalArgumentException("No inexistente: " + nodeId);
        }
        return node;
    }

    /**
     * Localiza uma aresta existente no agregado.
     *
     * @param edgeId identificador da aresta esperada
     * @return aresta encontrada
     */
    private GceEdge requireEdge(UUID edgeId) {
        GceEdge edge = edges.get(edgeId);
        if (edge == null) {
            throw new IllegalArgumentException("Aresta inexistente: " + edgeId);
        }
        return edge;
    }

    /**
     * Localiza uma restricao existente no agregado.
     *
     * @param restrictionId identificador da restricao esperada
     * @return restricao encontrada
     */
    private GceRestriction requireRestriction(UUID restrictionId) {
        GceRestriction restriction = restrictions.get(restrictionId);
        if (restriction == null) {
            throw new IllegalArgumentException("Restricao inexistente: " + restrictionId);
        }
        return restriction;
    }

    /**
     * Verifica se um codigo de no ainda esta disponivel no agregado.
     *
     * @param code codigo a ser verificado
     * @param ignoredNodeId identificador a ser ignorado na busca, quando aplicavel
     */
    private void ensureNodeCodeAvailable(String code, UUID ignoredNodeId) {
        boolean alreadyInUse = nodes.values().stream()
                .anyMatch(node -> node.getCode().equals(code) && !node.getId().equals(ignoredNodeId));

        if (alreadyInUse) {
            throw new IllegalArgumentException("Ja existe no com o codigo informado: " + code);
        }
    }

    /**
     * Verifica se a assinatura de uma aresta ainda nao foi usada no agregado.
     *
     * @param edge aresta candidata
     * @param ignoredEdgeId identificador a ser ignorado na comparacao, quando aplicavel
     */
    private void ensureEdgeSignatureAvailable(GceEdge edge, UUID ignoredEdgeId) {
        boolean alreadyInUse = edges.values().stream()
                .anyMatch(existingEdge -> existingEdge.sameSignature(edge) && !existingEdge.getId().equals(ignoredEdgeId));

        if (alreadyInUse) {
            throw new IllegalArgumentException("Ja existe aresta com mesma origem, destino e tipo.");
        }
    }

    /**
     * Verifica se a definicao de uma restricao ainda nao foi usada no agregado.
     *
     * @param restriction restricao candidata
     * @param ignoredRestrictionId identificador a ser ignorado na comparacao, quando aplicavel
     */
    private void ensureRestrictionSignatureAvailable(GceRestriction restriction, UUID ignoredRestrictionId) {
        boolean alreadyInUse = restrictions.values().stream()
                .anyMatch(existingRestriction -> existingRestriction.sameDefinition(restriction)
                        && !existingRestriction.getId().equals(ignoredRestrictionId));

        if (alreadyInUse) {
            throw new IllegalArgumentException("Ja existe restricao com mesmo tipo e mesmos nos.");
        }
    }

    /**
     * Retorna o identificador do grafo.
     *
     * @return identificador do agregado
     */
    public UUID getId() {
        return id;
    }

    /**
     * Retorna o identificador do projeto ao qual o grafo pertence.
     *
     * @return identificador do projeto
     */
    public UUID getProjectId() {
        return projectId;
    }

    /**
     * Retorna o nome do grafo.
     *
     * @return nome do GCE
     */
    public String getName() {
        return name;
    }

    /**
     * Retorna a descricao do grafo.
     *
     * @return descricao textual do GCE
     */
    public String getDescription() {
        return description;
    }

    /**
     * Indica se o grafo esta marcado como selecionado.
     *
     * @return {@code true} quando selecionado
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Retorna os nos do grafo em visao imutavel.
     *
     * @return colecao de nos
     */
    public Collection<GceNode> getNodes() {
        return Collections.unmodifiableCollection(nodes.values());
    }

    /**
     * Retorna as arestas do grafo em visao imutavel.
     *
     * @return colecao de arestas
     */
    public Collection<GceEdge> getEdges() {
        return Collections.unmodifiableCollection(edges.values());
    }

    /**
     * Retorna as restricoes do grafo em visao imutavel.
     *
     * @return colecao de restricoes
     */
    public Collection<GceRestriction> getRestrictions() {
        return Collections.unmodifiableCollection(restrictions.values());
    }

    /**
     * Busca um no pelo identificador.
     *
     * @param nodeId identificador do no
     * @return no encontrado, quando existir
     */
    public Optional<GceNode> findNode(UUID nodeId) {
        return Optional.ofNullable(nodes.get(nodeId));
    }

    /**
     * Busca uma aresta pelo identificador.
     *
     * @param edgeId identificador da aresta
     * @return aresta encontrada, quando existir
     */
    public Optional<GceEdge> findEdge(UUID edgeId) {
        return Optional.ofNullable(edges.get(edgeId));
    }

    /**
     * Busca uma restricao pelo identificador.
     *
     * @param restrictionId identificador da restricao
     * @return restricao encontrada, quando existir
     */
    public Optional<GceRestriction> findRestriction(UUID restrictionId) {
        return Optional.ofNullable(restrictions.get(restrictionId));
    }

    /**
     * Retorna todos os nos de causa do grafo.
     *
     * @return lista de causas
     */
    public List<GceNode> getCauseNodes() {
        return nodes.values().stream().filter(GceNode::isCause).toList();
    }

    /**
     * Retorna todos os nos de efeito do grafo.
     *
     * @return lista de efeitos
     */
    public List<GceNode> getEffectNodes() {
        return nodes.values().stream().filter(GceNode::isEffect).toList();
    }

    /**
     * Retorna todos os nos operadores do grafo.
     *
     * @return lista de operadores
     */
    public List<GceNode> getOperatorNodes() {
        return nodes.values().stream().filter(GceNode::isOperator).toList();
    }

    /**
     * Retorna as arestas de entrada de um no.
     *
     * @param nodeId identificador do no de destino
     * @return lista de arestas que chegam ao no
     */
    public List<GceEdge> incomingEdges(UUID nodeId) {
        return edges.values().stream()
                .filter(edge -> edge.targets(nodeId))
                .toList();
    }

    /**
     * Retorna as arestas de saida de um no.
     *
     * @param nodeId identificador do no de origem
     * @return lista de arestas que partem do no
     */
    public List<GceEdge> outgoingEdges(UUID nodeId) {
        return edges.values().stream()
                .filter(edge -> edge.startsFrom(nodeId))
                .toList();
    }

    /**
     * Conta a frequencia de cada codigo de no presente no agregado.
     *
     * @return mapa contendo codigo e quantidade de ocorrencias
     */
    public Map<String, Long> countNodeCodes() {
        return nodes.values().stream()
                .collect(Collectors.groupingBy(GceNode::getCode, Collectors.counting()));
    }

    /**
     * Atualiza os metadados textuais do grafo.
     *
     * @param name novo nome do grafo
     * @param description nova descricao do grafo
     */
    public void updateDetails(String name, String description) {
        this.name = requireText(name, "name");
        this.description = normalizeDescription(description);
    }

    /**
     * Marca o grafo como selecionado.
     */
    public void select() {
        this.selected = true;
    }

    /**
     * Remove a marcacao de selecionado do grafo.
     */
    public void unselect() {
        this.selected = false;
    }

    /**
     * Adiciona um novo no ao agregado.
     *
     * @param node no a ser incorporado
     */
    public void addNode(GceNode node) {
        if (node == null) {
            throw new IllegalArgumentException("node e obrigatorio.");
        }
        if (nodes.containsKey(node.getId())) {
            throw new IllegalArgumentException("Ja existe no com o id informado: " + node.getId());
        }

        ensureNodeCodeAvailable(node.getCode(), null);
        nodes.put(node.getId(), node);
    }

    /**
     * Substitui um no existente mantendo a consistencia do agregado.
     *
     * @param node nova representacao do no
     */
    public void replaceNode(GceNode node) {
        if (node == null) {
            throw new IllegalArgumentException("node e obrigatorio.");
        }
        GceNode previousNode = requireNode(node.getId());

        ensureNodeCodeAvailable(node.getCode(), node.getId());
        nodes.put(node.getId(), node);

        try {
            validateAggregate();
        } catch (RuntimeException exception) {
            nodes.put(previousNode.getId(), previousNode);
            throw exception;
        }
    }

    /**
     * Remove um no que nao esteja referenciado por arestas ou restricoes.
     *
     * @param nodeId identificador do no a ser removido
     */
    public void removeNode(UUID nodeId) {
        UUID requiredNodeId = requireUuid(nodeId, "nodeId");
        GceNode node = requireNode(requiredNodeId);

        boolean hasAttachedEdges = edges.values().stream().anyMatch(edge -> edge.references(requiredNodeId));
        if (hasAttachedEdges) {
            throw new IllegalArgumentException("Nao e permitido remover o no " + node.getCode() + " enquanto houver arestas associadas.");
        }

        boolean hasAttachedRestrictions = restrictions.values().stream().anyMatch(restriction -> restriction.references(requiredNodeId));
        if (hasAttachedRestrictions) {
            throw new IllegalArgumentException("Nao e permitido remover o no " + node.getCode() + " enquanto houver restricoes associadas.");
        }

        nodes.remove(requiredNodeId);
    }

    /**
     * Adiciona uma nova aresta ao agregado.
     *
     * @param edge aresta a ser adicionada
     */
    public void addEdge(GceEdge edge) {
        if (edge == null) {
            throw new IllegalArgumentException("edge e obrigatorio.");
        }
        if (edges.containsKey(edge.getId())) {
            throw new IllegalArgumentException("Ja existe aresta com o id informado: " + edge.getId());
        }

        requireNode(edge.getSourceNodeId());
        requireNode(edge.getTargetNodeId());
        ensureEdgeSignatureAvailable(edge, null);

        edges.put(edge.getId(), edge);

        try {
            validateAggregate();
        } catch (RuntimeException exception) {
            edges.remove(edge.getId());
            throw exception;
        }
    }

    /**
     * Substitui uma aresta existente mantendo a consistencia estrutural do grafo.
     *
     * @param edge nova representacao da aresta
     */
    public void replaceEdge(GceEdge edge) {
        if (edge == null) {
            throw new IllegalArgumentException("edge e obrigatorio.");
        }
        GceEdge previousEdge = requireEdge(edge.getId());

        requireNode(edge.getSourceNodeId());
        requireNode(edge.getTargetNodeId());
        ensureEdgeSignatureAvailable(edge, edge.getId());

        edges.put(edge.getId(), edge);

        try {
            validateAggregate();
        } catch (RuntimeException exception) {
            edges.put(previousEdge.getId(), previousEdge);
            throw exception;
        }
    }

    /**
     * Remove uma aresta existente do agregado.
     *
     * @param edgeId identificador da aresta a ser removida
     */
    public void removeEdge(UUID edgeId) {
        UUID requiredEdgeId = requireUuid(edgeId, "edgeId");
        requireEdge(requiredEdgeId);
        edges.remove(requiredEdgeId);
    }

    /**
     * Adiciona uma nova restricao ao agregado.
     *
     * @param restriction restricao a ser adicionada
     */
    public void addRestriction(GceRestriction restriction) {
        if (restriction == null) {
            throw new IllegalArgumentException("restriction e obrigatorio.");
        }
        if (restrictions.containsKey(restriction.getId())) {
            throw new IllegalArgumentException("Ja existe restricao com o id informado: " + restriction.getId());
        }

        restriction.getNodeIds().forEach(this::requireNode);
        ensureRestrictionSignatureAvailable(restriction, null);

        restrictions.put(restriction.getId(), restriction);

        try {
            validateAggregate();
        } catch (RuntimeException exception) {
            restrictions.remove(restriction.getId());
            throw exception;
        }
    }

    /**
     * Substitui uma restricao existente mantendo a consistencia do agregado.
     *
     * @param restriction nova representacao da restricao
     */
    public void replaceRestriction(GceRestriction restriction) {
        if (restriction == null) {
            throw new IllegalArgumentException("restriction e obrigatorio.");
        }
        GceRestriction previousRestriction = requireRestriction(restriction.getId());

        restriction.getNodeIds().forEach(this::requireNode);
        ensureRestrictionSignatureAvailable(restriction, restriction.getId());

        restrictions.put(restriction.getId(), restriction);

        try {
            validateAggregate();
        } catch (RuntimeException exception) {
            restrictions.put(previousRestriction.getId(), previousRestriction);
            throw exception;
        }
    }

    /**
     * Remove uma restricao existente do agregado.
     *
     * @param restrictionId identificador da restricao a ser removida
     */
    public void removeRestriction(UUID restrictionId) {
        UUID requiredRestrictionId = requireUuid(restrictionId, "restrictionId");
        requireRestriction(requiredRestrictionId);
        restrictions.remove(requiredRestrictionId);
    }
}
