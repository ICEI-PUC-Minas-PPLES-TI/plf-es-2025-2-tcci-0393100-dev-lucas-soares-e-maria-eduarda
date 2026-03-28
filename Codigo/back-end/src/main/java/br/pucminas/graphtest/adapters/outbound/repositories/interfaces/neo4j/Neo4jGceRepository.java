package br.pucminas.graphtest.adapters.outbound.repositories.interfaces.neo4j;

import br.pucminas.graphtest.adapters.outbound.entities.Neo4jGceEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repositorio Spring Data Neo4j para a entidade raiz do GCE.
 */
@Repository
public interface Neo4jGceRepository extends Neo4jRepository<Neo4jGceEntity, UUID> {

    @Query("""
            MATCH (g:Gce {projectId: $projectId})
            OPTIONAL MATCH (g)-[:HAS_NODE]->(n:GceNode)
            OPTIONAL MATCH (g)-[:HAS_RESTRICTION]->(r:GceRestriction)
            DETACH DELETE g, n, r
            """)
    void deleteAllByProjectId(UUID projectId);
}
