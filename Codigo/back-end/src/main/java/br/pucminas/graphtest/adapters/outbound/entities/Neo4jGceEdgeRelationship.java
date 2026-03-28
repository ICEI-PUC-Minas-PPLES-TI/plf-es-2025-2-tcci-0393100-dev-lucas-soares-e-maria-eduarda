package br.pucminas.graphtest.adapters.outbound.entities;

import br.pucminas.graphtest.application.domain.enums.GceEdgeTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.util.UUID;

/**
 * Relacionamento Neo4j que representa uma aresta do GCE.
 */
@RelationshipProperties
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Neo4jGceEdgeRelationship {

    @Id
    @GeneratedValue
    private Long relationshipId;

    private UUID edgeId;

    private GceEdgeTypeEnum type;

    @TargetNode
    private Neo4jGceNodeEntity targetNode;
}
