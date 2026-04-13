package br.pucminas.graphtest.adapters.outbound.repositories.neo4j.mapper;

import br.pucminas.graphtest.adapters.outbound.entities.neo4j.gce.Neo4jGceEdgeRelationship;
import br.pucminas.graphtest.adapters.outbound.entities.neo4j.gce.Neo4jGceEntity;
import br.pucminas.graphtest.adapters.outbound.entities.neo4j.gce.Neo4jGceNodeEntity;
import br.pucminas.graphtest.adapters.outbound.entities.neo4j.gce.Neo4jGceRestrictionEntity;
import br.pucminas.graphtest.adapters.outbound.repositories.shared.BasePersistenceMapper;
import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.domain.gce.model.GceEdge;
import br.pucminas.graphtest.application.domain.gce.model.GceNode;
import br.pucminas.graphtest.application.domain.gce.model.GceRestriction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Mapper responsavel por converter GCEs entre dominio e persistencia Neo4j.
 */
@Component
public class GceMapperBase implements BasePersistenceMapper<Gce, Neo4jGceEntity> {

    @Override
    public Neo4jGceEntity toEntity(Gce graph) {
        if (graph == null) {
            return null;
        }

        Neo4jGceEntity entity = new Neo4jGceEntity();
        applyId(entity, graph.getId());
        entity.setProjectId(graph.getProjectId());
        entity.setName(graph.getName());
        entity.setDescription(graph.getDescription());
        entity.setSelected(graph.isSelected());

        Map<String, Neo4jGceNodeEntity> nodeEntities = new LinkedHashMap<>();
        for (GceNode node : graph.getNodes()) {
            Neo4jGceNodeEntity nodeEntity = toNodeEntity(node);
            nodeEntity.setOutgoingEdges(new ArrayList<>());
            nodeEntities.put(node.getCode(), nodeEntity);
        }

        for (GceEdge edge : graph.getEdges()) {
            Neo4jGceNodeEntity sourceNode = nodeEntities.get(edge.getSourceNodeCode());
            Neo4jGceNodeEntity targetNode = nodeEntities.get(edge.getTargetNodeCode());
            if (sourceNode == null || targetNode == null) {
                continue;
            }

            Neo4jGceEdgeRelationship relationship = new Neo4jGceEdgeRelationship();
            relationship.setEdgeId(edge.getId());
            relationship.setType(edge.getType());
            relationship.setTargetNode(targetNode);
            sourceNode.getOutgoingEdges().add(relationship);
        }

        List<Neo4jGceRestrictionEntity> restrictionEntities = graph.getRestrictions().stream()
                .map(restriction -> toRestrictionEntity(restriction, nodeEntities))
                .toList();

        entity.setNodes(new ArrayList<>(nodeEntities.values()));
        entity.setRestrictions(new ArrayList<>(restrictionEntities));
        return entity;
    }

    @Override
    public Gce toDomain(Neo4jGceEntity entity) {
        if (entity == null) {
            return null;
        }

        List<GceNode> nodes = entity.getNodes() == null
                ? List.of()
                : entity.getNodes().stream()
                .map(this::toDomainNode)
                .toList();

        List<GceEdge> edges = new ArrayList<>();
        if (entity.getNodes() != null) {
            for (Neo4jGceNodeEntity sourceNode : entity.getNodes()) {
                if (sourceNode.getOutgoingEdges() == null) {
                    continue;
                }

                for (Neo4jGceEdgeRelationship edgeRelationship : sourceNode.getOutgoingEdges()) {
                    if (edgeRelationship == null || edgeRelationship.getTargetNode() == null) {
                        continue;
                    }

                    edges.add(new GceEdge(
                            edgeRelationship.getEdgeId(),
                            sourceNode.getCode(),
                            edgeRelationship.getTargetNode().getCode(),
                            edgeRelationship.getType()
                    ));
                }
            }
        }

        List<GceRestriction> restrictions = entity.getRestrictions() == null
                ? List.of()
                : entity.getRestrictions().stream()
                .map(this::toDomainRestriction)
                .toList();

        return new Gce(
                entity.getId(),
                entity.getProjectId(),
                entity.getName(),
                entity.getDescription(),
                Boolean.TRUE.equals(entity.getSelected()),
                nodes,
                edges,
                restrictions
        );
    }

    private Neo4jGceRestrictionEntity toRestrictionEntity(
            GceRestriction restriction,
            Map<String, Neo4jGceNodeEntity> nodeEntities
    ) {
        Neo4jGceRestrictionEntity entity = new Neo4jGceRestrictionEntity();
        applyId(entity, restriction.getId());
        entity.setType(restriction.getType());
        entity.setAppliesTo(restriction.getNodeCodes().stream()
                .map(nodeEntities::get)
                .filter(java.util.Objects::nonNull)
                .toList());
        return entity;
    }

    private Neo4jGceNodeEntity toNodeEntity(GceNode node) {
        Neo4jGceNodeEntity entity = new Neo4jGceNodeEntity();
        applyId(entity, node.getId());
        entity.setCode(node.getCode());
        entity.setLabel(node.getLabel());
        entity.setType(node.getType());
        entity.setOperatorType(node.getOperatorType());
        return entity;
    }

    private void applyId(Neo4jGceEntity entity, java.util.UUID id) {
        if (id != null) {
            entity.setId(id);
        }
    }

    private void applyId(Neo4jGceNodeEntity entity, java.util.UUID id) {
        if (id != null) {
            entity.setId(id);
        }
    }

    private void applyId(Neo4jGceRestrictionEntity entity, java.util.UUID id) {
        if (id != null) {
            entity.setId(id);
        }
    }

    private GceNode toDomainNode(Neo4jGceNodeEntity entity) {
        return new GceNode(
                entity.getId(),
                entity.getCode(),
                entity.getLabel(),
                entity.getType(),
                entity.getOperatorType()
        );
    }

    private GceRestriction toDomainRestriction(Neo4jGceRestrictionEntity entity) {
        return new GceRestriction(
                entity.getId(),
                entity.getType(),
                entity.getAppliesTo() == null
                        ? List.of()
                        : entity.getAppliesTo().stream()
                        .map(Neo4jGceNodeEntity::getCode)
                        .toList()
        );
    }

}
