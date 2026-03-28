package br.pucminas.graphtest.adapters.outbound.repositories.mappers;

import br.pucminas.graphtest.adapters.outbound.entities.Neo4jGceEdgeRelationship;
import br.pucminas.graphtest.adapters.outbound.entities.Neo4jGceEntity;
import br.pucminas.graphtest.adapters.outbound.entities.Neo4jGceNodeEntity;
import br.pucminas.graphtest.adapters.outbound.entities.Neo4jGceRestrictionEntity;
import br.pucminas.graphtest.application.domain.Gce;
import br.pucminas.graphtest.application.domain.GceEdge;
import br.pucminas.graphtest.application.domain.GceNode;
import br.pucminas.graphtest.application.domain.GceRestriction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Mapper responsavel por converter GCEs entre dominio e persistencia Neo4j.
 */
@Component
public class GceMapper implements PersistenceMapper<Gce, Neo4jGceEntity> {

    @Override
    public Neo4jGceEntity toEntity(Gce graph) {
        if (graph == null) {
            return null;
        }

        Neo4jGceEntity entity = new Neo4jGceEntity();
        entity.setId(graph.getId());
        entity.setProjectId(graph.getProjectId());
        entity.setName(graph.getName());
        entity.setDescription(graph.getDescription());
        entity.setSelected(graph.isSelected());

        Map<String, Neo4jGceNodeEntity> nodeEntities = new LinkedHashMap<>();
        for (GceNode node : graph.getNodes()) {
            Neo4jGceNodeEntity nodeEntity = new Neo4jGceNodeEntity();
            nodeEntity.setId(node.getId());
            nodeEntity.setCode(node.getCode());
            nodeEntity.setLabel(node.getLabel());
            nodeEntity.setType(node.getType());
            nodeEntity.setOperatorType(node.getOperatorType());
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
            relationship.setId(edge.getId());
            relationship.setType(edge.getType());
            relationship.setTargetNode(targetNode);

            sourceNode.getOutgoingEdges().add(relationship);
        }

        List<Neo4jGceRestrictionEntity> restrictionEntities = graph.getRestrictions().stream()
                .map(this::toRestrictionEntity)
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
                            edgeRelationship.getId(),
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

    private Neo4jGceRestrictionEntity toRestrictionEntity(GceRestriction restriction) {
        Neo4jGceRestrictionEntity entity = new Neo4jGceRestrictionEntity();
        entity.setId(restriction.getId());
        entity.setType(restriction.getType());
        entity.setNodeCodes(new ArrayList<>(restriction.getNodeCodes()));
        return entity;
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
                entity.getNodeCodes()
        );
    }
}
