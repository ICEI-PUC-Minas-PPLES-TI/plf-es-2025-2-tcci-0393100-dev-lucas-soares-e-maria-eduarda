package br.pucminas.graphtest.adapters.outbound.entities.neo4j.gce;

import br.pucminas.graphtest.adapters.outbound.entities.neo4j.shared.Neo4jBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidade Neo4j que representa o agregado raiz de um GCE.
 */
@Node("Gce")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString
public class Neo4jGceEntity extends Neo4jBaseEntity {

    private UUID projectId;
    private String name;
    private String description;
    private Boolean selected;

    @Relationship(type = "HAS_NODE")
    private List<Neo4jGceNodeEntity> nodes = new ArrayList<>();

    @Relationship(type = "HAS_RESTRICTION")
    private List<Neo4jGceRestrictionEntity> restrictions = new ArrayList<>();

    @Override
    public void prepareAuditForSave() {
        super.prepareAuditForSave();
        prepareNodesForSave();
        prepareRestrictionsForSave();
    }

    private void prepareNodesForSave() {
        if (nodes == null) {
            return;
        }

        for (Neo4jGceNodeEntity node : nodes) {
            if (node == null) {
                continue;
            }
            node.prepareAuditForSave();
            if (node.getOutgoingEdges() == null) {
                continue;
            }
            node.getOutgoingEdges().stream()
                    .filter(java.util.Objects::nonNull)
                    .forEach(Neo4jGceEdgeRelationship::prepareAuditForSave);
        }
    }

    private void prepareRestrictionsForSave() {
        if (restrictions == null) {
            return;
        }

        restrictions.stream()
                .filter(java.util.Objects::nonNull)
                .forEach(Neo4jGceRestrictionEntity::prepareAuditForSave);
    }
}
