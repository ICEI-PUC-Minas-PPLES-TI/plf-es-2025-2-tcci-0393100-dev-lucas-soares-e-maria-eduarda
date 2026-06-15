package br.pucminas.graphtest.adapters.outbound.entities.neo4j.gce;

import br.pucminas.graphtest.application.domain.gce.enums.GceEdgeTypeEnum;
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

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @TargetNode
    private Neo4jGceNodeEntity targetNode;

    public void prepareAuditForSave() {
        if (createdAt == null || updatedAt == null) {
            LocalDateTime now = LocalDateTime.now();
            if (createdAt == null) {
                createdAt = now;
            }
            if (updatedAt == null) {
                updatedAt = createdAt;
            }
            return;
        }

        updatedAt = LocalDateTime.now();
    }
}
