package br.pucminas.graphtest.adapters.outbound.repositories.neo4j;

import br.pucminas.graphtest.adapters.outbound.entities.neo4j.gfc.Neo4jGfcEntity;
import br.pucminas.graphtest.adapters.outbound.repositories.neo4j.interfaces.Neo4jGfcRepository;
import br.pucminas.graphtest.adapters.outbound.repositories.neo4j.mapper.GfcMapperBase;
import br.pucminas.graphtest.application.domain.gfc.model.Gfc;
import br.pucminas.graphtest.application.port.output.repositories.GfcRepositoryPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador de saida responsavel por persistir GFCs no Neo4j.
 */
@Repository
@AllArgsConstructor
public class GfcRepositoryAdapter implements GfcRepositoryPort {

    private final Neo4jGfcRepository neo4jGfcRepository;
    private final GfcMapperBase mapper;

    @Override
    public Gfc save(Gfc graph) {
        Neo4jGfcEntity entityToSave = mapper.toEntity(graph);
        return mapper.toDomain(neo4jGfcRepository.save(entityToSave));
    }

    @Override
    public Optional<Gfc> findById(UUID id) {
        return neo4jGfcRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Gfc> findAllByProjectId(UUID projectId) {
        return neo4jGfcRepository.findAllByProjectIdOrderByCreatedAtDesc(projectId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        neo4jGfcRepository.deleteGraphById(id);
    }

    @Override
    public void deleteAllByProjectId(UUID projectId) {
        neo4jGfcRepository.deleteAllByProjectId(projectId);
    }

    @Override
    public void deleteAllBySourceFileId(UUID sourceFileId) {
        neo4jGfcRepository.deleteAllBySourceFileId(sourceFileId);
    }
}
