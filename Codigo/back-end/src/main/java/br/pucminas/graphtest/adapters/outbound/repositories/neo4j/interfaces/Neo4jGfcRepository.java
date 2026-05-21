package br.pucminas.graphtest.adapters.outbound.repositories.neo4j.interfaces;

import br.pucminas.graphtest.adapters.outbound.entities.neo4j.gfc.Neo4jGfcEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio Spring Data Neo4j para a entidade raiz do GFC.
 */
@Repository
public interface Neo4jGfcRepository extends Neo4jRepository<Neo4jGfcEntity, UUID> {

    List<Neo4jGfcEntity> findAllByProjectIdOrderByCreatedAtDesc(UUID projectId);

    @Query("""
            MATCH (g:Gfc {id: $id})
            OPTIONAL MATCH (g)-[:HAS_NODE]->(n:GfcNode)
            DETACH DELETE g, n
            """)
    void deleteGraphById(UUID id);

    @Query("""
            MATCH (g:Gfc {sourceFileId: $sourceFileId})
            OPTIONAL MATCH (g)-[:HAS_NODE]->(n:GfcNode)
            DETACH DELETE g, n
            """)
    void deleteAllBySourceFileId(UUID sourceFileId);
}
