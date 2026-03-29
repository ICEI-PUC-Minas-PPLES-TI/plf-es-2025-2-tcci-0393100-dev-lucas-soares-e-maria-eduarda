package br.pucminas.graphtest.adapters.outbound.repositories;

import br.pucminas.graphtest.adapters.outbound.entities.Neo4jGceEntity;
import br.pucminas.graphtest.adapters.outbound.repositories.interfaces.neo4j.Neo4jGceRepository;
import br.pucminas.graphtest.adapters.outbound.repositories.mappers.GceMapper;
import br.pucminas.graphtest.application.domain.Gce;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador de saida responsavel por persistir GCEs no Neo4j.
 */
@Repository
@AllArgsConstructor
public class GceRepositoryPortImpl implements GceRepositoryPort {

    private final Neo4jGceRepository neo4jGceRepository;
    private final GceMapper mapper;

    @Override
    public Gce save(Gce graph) {
        Neo4jGceEntity entityToSave = mapper.toEntity(graph);
        return mapper.toDomain(neo4jGceRepository.save(entityToSave));
    }

    @Override
    public Optional<Gce> findById(UUID id) {
        return neo4jGceRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public void deleteAllByProjectId(UUID projectId) {
        neo4jGceRepository.deleteAllByProjectId(projectId);
    }
}
