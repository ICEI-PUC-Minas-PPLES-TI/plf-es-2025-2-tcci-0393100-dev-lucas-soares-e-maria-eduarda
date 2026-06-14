package br.pucminas.graphtest.adapters.outbound.repositories.neo4j.interfaces;

import br.pucminas.graphtest.adapters.outbound.entities.neo4j.gce.Neo4jGceEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio Spring Data Neo4j para a entidade raiz do GCE.
 */
@Repository
public interface Neo4jGceRepository extends Neo4jRepository<Neo4jGceEntity, UUID> {

    List<Neo4jGceEntity> findAllByProjectId(UUID projectId);

    @Query("""
            MATCH (g:Gce {id: $id})
            OPTIONAL MATCH (g)-[:HAS_NODE]->(n:GceNode)
            OPTIONAL MATCH (g)-[:HAS_RESTRICTION]->(r:GceRestriction)
            DETACH DELETE g, n, r
            """)
    void deleteGraphById(UUID id);

    @Query("""
            MATCH (g:Gce {projectId: $projectId})
            OPTIONAL MATCH (g)-[:HAS_NODE]->(n:GceNode)
            OPTIONAL MATCH (g)-[:HAS_RESTRICTION]->(r:GceRestriction)
            DETACH DELETE g, n, r
            """)
    void deleteAllByProjectId(UUID projectId);
}
