package br.pucminas.graphtest.adapters.outbound.repositories;

import br.pucminas.graphtest.adapters.outbound.entities.Neo4jGceEntity;
import br.pucminas.graphtest.adapters.outbound.repositories.interfaces.Neo4jGceRepository;
import br.pucminas.graphtest.adapters.outbound.repositories.mappers.GceMapper;
import br.pucminas.graphtest.application.domain.Gce;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import lombok.AllArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador de saida responsavel por persistir GCEs no Neo4j.
 */
@AllArgsConstructor
public class GceRepositoryPortImpl implements GceRepositoryPort {

    private final Neo4jGceRepository neo4jGceRepository;
    private final GceMapper mapper;

    /**
     * Persiste o agregado informado no Neo4j.
     *
     * @param graph agregado a ser salvo
     * @return o proprio agregado persistido
     */
    @Override
    public Gce save(Gce graph) {
        neo4jGceRepository.deleteGraphById(graph.getId());
        neo4jGceRepository.createGraphRoot(
                graph.getId(),
                graph.getProjectId(),
                graph.getName(),
                graph.getDescription(),
                graph.isSelected()
        );
        neo4jGceRepository.createGraphNodes(graph.getId(), createNodePayload(graph));
        neo4jGceRepository.createGraphRestrictions(graph.getId(), createRestrictionPayload(graph));
        neo4jGceRepository.createGraphEdges(createEdgePayload(graph));
        return graph;
    }

    /**
     * Busca um GCE pelo identificador.
     *
     * @param id identificador do grafo
     * @return agregado encontrado, quando existir
     */
    @Override
    public Optional<Gce> findById(UUID id) {
        return neo4jGceRepository.findById(id)
                .map(mapper::toDomain);
    }

    private List<Map<String, Object>> createNodePayload(Gce graph) {
        return graph.getNodes().stream()
                .map(node -> {
                    Map<String, Object> values = new LinkedHashMap<>();
                    values.put("id", node.getId());
                    values.put("code", node.getCode());
                    values.put("label", node.getLabel());
                    values.put("type", node.getType().name());
                    values.put("operatorType", node.getOperatorType() != null ? node.getOperatorType().name() : null);
                    return values;
                })
                .toList();
    }

    private List<Map<String, Object>> createRestrictionPayload(Gce graph) {
        return graph.getRestrictions().stream()
                .map(restriction -> {
                    Map<String, Object> values = new LinkedHashMap<>();
                    values.put("id", restriction.getId());
                    values.put("type", restriction.getType().name());
                    values.put("nodeIds", restriction.getNodeIds());
                    return values;
                })
                .toList();
    }

    private List<Map<String, Object>> createEdgePayload(Gce graph) {
        return graph.getEdges().stream()
                .map(edge -> {
                    Map<String, Object> values = new LinkedHashMap<>();
                    values.put("edgeId", edge.getId());
                    values.put("sourceNodeId", edge.getSourceNodeId());
                    values.put("targetNodeId", edge.getTargetNodeId());
                    values.put("type", edge.getType().name());
                    return values;
                })
                .toList();
    }
}
