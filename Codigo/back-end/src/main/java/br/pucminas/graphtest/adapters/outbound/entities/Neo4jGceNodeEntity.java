package br.pucminas.graphtest.adapters.outbound.entities;

import br.pucminas.graphtest.application.domain.enums.GceNodeTypeEnum;
import br.pucminas.graphtest.application.domain.enums.GceOperatorTypeEnum;
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
 * Entidade Neo4j que representa um no pertencente a um GCE.
 */
@Node("GceNode")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = "outgoingEdges")
public class Neo4jGceNodeEntity extends Neo4jBaseEntity {

    private String code;
    private String label;
    private GceNodeTypeEnum type;
    private GceOperatorTypeEnum operatorType;

    @Relationship(type = "CONNECTS_TO")
    private List<Neo4jGceEdgeRelationship> outgoingEdges = new ArrayList<>();
}
