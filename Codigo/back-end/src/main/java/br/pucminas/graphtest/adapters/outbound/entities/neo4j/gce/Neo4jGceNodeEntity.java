package br.pucminas.graphtest.adapters.outbound.entities.neo4j.gce;

import br.pucminas.graphtest.adapters.outbound.entities.neo4j.shared.Neo4jBaseEntity;
import br.pucminas.graphtest.application.domain.gce.enums.GceNodeTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.GceOperatorTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Neo4j que representa um no pertencente a um GCE.
 */
@Node("GceNode")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(exclude = "outgoingEdges")
public class Neo4jGceNodeEntity extends Neo4jBaseEntity {

    private String code;
    private String label;
    private GceNodeTypeEnum type;
    private GceOperatorTypeEnum operatorType;

    @Relationship(type = "CONNECTS_TO", direction = Relationship.Direction.OUTGOING)
    private List<Neo4jGceEdgeRelationship> outgoingEdges = new ArrayList<>();
}
