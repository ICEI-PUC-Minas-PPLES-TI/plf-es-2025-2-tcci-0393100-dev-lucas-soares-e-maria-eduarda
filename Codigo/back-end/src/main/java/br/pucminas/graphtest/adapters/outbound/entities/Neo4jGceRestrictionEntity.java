package br.pucminas.graphtest.adapters.outbound.entities;

import br.pucminas.graphtest.application.domain.enums.RestrictionTypeEnum;
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
 * Entidade Neo4j que representa uma restricao associada ao GCE.
 */
@Node("GceRestriction")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(exclude = "appliesTo")
public class Neo4jGceRestrictionEntity extends Neo4jBaseEntity {

    private RestrictionTypeEnum type;

    @Relationship(type = "APPLIES_TO")
    private List<Neo4jGceNodeEntity> appliesTo = new ArrayList<>();
}
