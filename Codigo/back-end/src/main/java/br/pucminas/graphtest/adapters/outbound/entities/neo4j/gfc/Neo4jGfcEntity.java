package br.pucminas.graphtest.adapters.outbound.entities.neo4j.gfc;

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
 * Entidade Neo4j que representa o agregado raiz de um GFC.
 */
@Node("Gfc")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString
public class Neo4jGfcEntity extends Neo4jBaseEntity {

    private UUID projectId;
    private UUID sourceFileId;
    private String methodSignature;
    private String name;
    private String description;
    private String language;

    @Relationship(type = "HAS_NODE")
    private List<Neo4jGfcNodeEntity> nodes = new ArrayList<>();
}
