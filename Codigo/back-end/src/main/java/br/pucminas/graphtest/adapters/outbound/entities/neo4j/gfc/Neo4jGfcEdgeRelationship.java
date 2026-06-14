package br.pucminas.graphtest.adapters.outbound.entities.neo4j.gfc;

import br.pucminas.graphtest.application.domain.gfc.enums.GfcEdgeTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Relacionamento Neo4j que representa uma aresta do GFC.
 */
@RelationshipProperties
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "targetNode")
@ToString(exclude = "targetNode")
public class Neo4jGfcEdgeRelationship {

    @RelationshipId
    private Long id;

    private UUID edgeId;

    private GfcEdgeTypeEnum type;

    private String label;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @TargetNode
    private Neo4jGfcNodeEntity targetNode;
}
