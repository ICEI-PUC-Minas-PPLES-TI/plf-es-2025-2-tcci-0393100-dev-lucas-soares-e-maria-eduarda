package br.pucminas.graphtest.adapters.outbound.entities;

import br.pucminas.graphtest.application.domain.enums.GceEdgeTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.util.UUID;

/**
 * Relacionamento Neo4j que representa uma aresta do GCE.
 *
 * <p>O SDN exige um {@code @RelationshipId} interno do tipo {@link Long} para
 * classes anotadas com {@code @RelationshipProperties}. A identidade externa
 * da aresta usada pela aplicacao fica em {@code edgeId}.</p>
 */
@RelationshipProperties
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "targetNode")
@ToString(exclude = "targetNode")
public class Neo4jGceEdgeRelationship {

    @RelationshipId
    private Long id;

    private UUID edgeId;

    private GceEdgeTypeEnum type;

    @TargetNode
    private Neo4jGceNodeEntity targetNode;
}
