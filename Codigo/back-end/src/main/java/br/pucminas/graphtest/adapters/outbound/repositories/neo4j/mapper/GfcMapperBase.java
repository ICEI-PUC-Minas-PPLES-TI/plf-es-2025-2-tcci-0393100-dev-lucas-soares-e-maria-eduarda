package br.pucminas.graphtest.adapters.outbound.repositories.neo4j.mapper;

import br.pucminas.graphtest.adapters.outbound.entities.neo4j.gfc.Neo4jGfcEdgeRelationship;
import br.pucminas.graphtest.adapters.outbound.entities.neo4j.gfc.Neo4jGfcEntity;
import br.pucminas.graphtest.adapters.outbound.entities.neo4j.gfc.Neo4jGfcNodeEntity;
import br.pucminas.graphtest.adapters.outbound.repositories.shared.BasePersistenceMapper;
import br.pucminas.graphtest.application.domain.gfc.model.Gfc;
import br.pucminas.graphtest.application.domain.gfc.model.GfcEdge;
import br.pucminas.graphtest.application.domain.gfc.model.GfcNode;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Mapper responsavel por converter GFCs entre dominio e persistencia Neo4j.
 */
@Component
public class GfcMapperBase implements BasePersistenceMapper<Gfc, Neo4jGfcEntity> {

    @Override
    public Neo4jGfcEntity toEntity(Gfc graph) {
        if (graph == null) {
            return null;
        }

        Neo4jGfcEntity entity = new Neo4jGfcEntity();
        UUID graphId = graph.getId() != null ? graph.getId() : UUID.randomUUID();

        applyId(entity, graphId);
        applyAuditFields(entity, graph.getCreatedAt(), graph.getUpdatedAt());
        entity.setProjectId(graph.getProjectId());
        entity.setSourceFileId(graph.getSourceFileId());
        entity.setMethodSignature(graph.getMethodSignature());
        entity.setName(graph.getName());
        entity.setDescription(graph.getDescription());
        entity.setLanguage(graph.getLanguage());

        Map<String, Neo4jGfcNodeEntity> nodeEntities = new LinkedHashMap<>();
        for (GfcNode node : graph.getNodes()) {
            Neo4jGfcNodeEntity nodeEntity = toNodeEntity(node, graphId);
            nodeEntity.setOutgoingEdges(new ArrayList<>());
            nodeEntities.put(node.getCode(), nodeEntity);
        }

        for (GfcEdge edge : graph.getEdges()) {
            Neo4jGfcNodeEntity sourceNode = nodeEntities.get(edge.getSourceNodeCode());
            Neo4jGfcNodeEntity targetNode = nodeEntities.get(edge.getTargetNodeCode());
            if (sourceNode == null || targetNode == null) {
                continue;
            }

            Neo4jGfcEdgeRelationship relationship = new Neo4jGfcEdgeRelationship();
            relationship.setEdgeId(edge.getId());
            relationship.setType(edge.getType());
            relationship.setLabel(edge.getLabel());
            relationship.setCreatedAt(edge.getCreatedAt());
            relationship.setUpdatedAt(edge.getUpdatedAt());
            relationship.setTargetNode(targetNode);
            sourceNode.getOutgoingEdges().add(relationship);
        }

        entity.setNodes(new ArrayList<>(nodeEntities.values()));
        return entity;
    }

    @Override
    public Gfc toDomain(Neo4jGfcEntity entity) {
        if (entity == null) {
            return null;
        }

        List<GfcNode> nodes = entity.getNodes() == null
                ? List.of()
                : entity.getNodes().stream()
                .map(this::toDomainNode)
                .toList();

        List<GfcEdge> edges = new ArrayList<>();
        if (entity.getNodes() != null) {
            for (Neo4jGfcNodeEntity sourceNode : entity.getNodes()) {
                if (sourceNode.getOutgoingEdges() == null) {
                    continue;
                }

                for (Neo4jGfcEdgeRelationship edgeRelationship : sourceNode.getOutgoingEdges()) {
                    if (edgeRelationship == null || edgeRelationship.getTargetNode() == null) {
                        continue;
                    }

                    edges.add(new GfcEdge(
                            edgeRelationship.getEdgeId(),
                            sourceNode.getCode(),
                            edgeRelationship.getTargetNode().getCode(),
                            edgeRelationship.getType(),
                            edgeRelationship.getLabel(),
                            edgeRelationship.getCreatedAt(),
                            edgeRelationship.getUpdatedAt()
                    ));
                }
            }
        }

        return new Gfc(
                entity.getId(),
                entity.getProjectId(),
                entity.getSourceFileId(),
                entity.getMethodSignature(),
                entity.getName(),
                entity.getDescription(),
                entity.getLanguage(),
                nodes,
                edges,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private Neo4jGfcNodeEntity toNodeEntity(GfcNode node, UUID graphId) {
        Neo4jGfcNodeEntity entity = new Neo4jGfcNodeEntity();
        applyId(entity, node.getId());
        applyAuditFields(entity, node.getCreatedAt(), node.getUpdatedAt());
        entity.setGraphScopedCode(buildGraphScopedCode(graphId, node.getCode()));
        entity.setCode(node.getCode());
        entity.setLabel(node.getLabel());
        entity.setType(node.getType());
        entity.setStartLine(node.getStartLine());
        entity.setEndLine(node.getEndLine());
        return entity;
    }

    private GfcNode toDomainNode(Neo4jGfcNodeEntity entity) {
        return new GfcNode(
                entity.getId(),
                entity.getCode(),
                entity.getLabel(),
                entity.getType(),
                entity.getStartLine(),
                entity.getEndLine(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private String buildGraphScopedCode(UUID graphId, String nodeCode) {
        return graphId + ":" + nodeCode;
    }

    private void applyId(Neo4jGfcEntity entity, UUID id) {
        if (id != null) {
            entity.setId(id);
        }
    }

    private void applyAuditFields(Neo4jGfcEntity entity, LocalDateTime createdAt, LocalDateTime updatedAt) {
        entity.setCreatedAt(createdAt);
        entity.setUpdatedAt(updatedAt);
    }

    private void applyId(Neo4jGfcNodeEntity entity, UUID id) {
        if (id != null) {
            entity.setId(id);
        }
    }

    private void applyAuditFields(Neo4jGfcNodeEntity entity, LocalDateTime createdAt, LocalDateTime updatedAt) {
        entity.setCreatedAt(createdAt);
        entity.setUpdatedAt(updatedAt);
    }
}
