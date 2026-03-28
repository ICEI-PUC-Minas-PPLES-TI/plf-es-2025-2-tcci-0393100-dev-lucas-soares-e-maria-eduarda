package br.pucminas.graphtest.adapters.outbound.repositories.interfaces;

import br.pucminas.graphtest.adapters.outbound.entities.Neo4jGceEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio Spring Data Neo4j para a entidade raiz do GCE.
 */
@Repository
public interface Neo4jGceRepository extends Neo4jRepository<Neo4jGceEntity, Long> {}
