package br.pucminas.graphtest.adapters.outbound.entities.neo4j.gfc;

import br.pucminas.graphtest.adapters.outbound.entities.neo4j.shared.Neo4jBaseEntity;
import br.pucminas.graphtest.application.domain.gfc.enums.GfcNodeTypeEnum;
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
 * Entidade Neo4j que representa um no pertencente a um GFC.
 */
@Node("GfcNode")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(exclude = "outgoingEdges")
public class Neo4jGfcNodeEntity extends Neo4jBaseEntity {

    private String graphScopedCode;
    private String code;
    private String label;
    private GfcNodeTypeEnum type;
    private Integer startLine;
    private Integer endLine;

    @Relationship(type = "CONNECTS_TO", direction = Relationship.Direction.OUTGOING)
    private List<Neo4jGfcEdgeRelationship> outgoingEdges = new ArrayList<>();
}
