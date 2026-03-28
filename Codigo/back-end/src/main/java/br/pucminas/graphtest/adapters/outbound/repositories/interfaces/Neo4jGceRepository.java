package br.pucminas.graphtest.adapters.outbound.repositories.interfaces;

import br.pucminas.graphtest.adapters.outbound.entities.Neo4jGceEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Repositorio Spring Data Neo4j para a entidade raiz do GCE.
 */
@Repository
public interface Neo4jGceRepository extends Neo4jRepository<Neo4jGceEntity, UUID> {

    @Query("""
            MATCH (g:Gce {id: $id})
            OPTIONAL MATCH (g)-[:HAS_NODE]->(n:GceNode)
            OPTIONAL MATCH (g)-[:HAS_RESTRICTION]->(r:GceRestriction)
            DETACH DELETE g, n, r
            """)
    void deleteGraphById(@Param("id") UUID id);

    @Query("""
            CREATE (g:Gce {
                id: $id,
                projectId: $projectId,
                name: $name,
                description: $description,
                selected: $selected
            })
            """)
    void createGraphRoot(@Param("id") UUID id,
                         @Param("projectId") UUID projectId,
                         @Param("name") String name,
                         @Param("description") String description,
                         @Param("selected") boolean selected);

    @Query("""
            MATCH (g:Gce {id: $graphId})
            UNWIND $nodes AS node
            CREATE (n:GceNode {
                id: node.id,
                code: node.code,
                label: node.label,
                type: node.type,
                operatorType: node.operatorType
            })
            CREATE (g)-[:HAS_NODE]->(n)
            """)
    void createGraphNodes(@Param("graphId") UUID graphId,
                          @Param("nodes") List<Map<String, Object>> nodes);

    @Query("""
            MATCH (g:Gce {id: $graphId})
            UNWIND $restrictions AS restriction
            CREATE (r:GceRestriction {
                id: restriction.id,
                type: restriction.type,
                nodeIds: restriction.nodeIds
            })
            CREATE (g)-[:HAS_RESTRICTION]->(r)
            """)
    void createGraphRestrictions(@Param("graphId") UUID graphId,
                                 @Param("restrictions") List<Map<String, Object>> restrictions);

    @Query("""
            UNWIND $edges AS edge
            MATCH (source:GceNode {id: edge.sourceNodeId})
            MATCH (target:GceNode {id: edge.targetNodeId})
            CREATE (source)-[:CONNECTS_TO {
                edgeId: edge.edgeId,
                type: edge.type
            }]->(target)
            """)
    void createGraphEdges(@Param("edges") List<Map<String, Object>> edges);
}
