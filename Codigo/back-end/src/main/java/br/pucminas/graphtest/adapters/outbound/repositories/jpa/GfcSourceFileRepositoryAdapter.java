package br.pucminas.graphtest.adapters.outbound.repositories.jpa;

import br.pucminas.graphtest.adapters.outbound.entities.jpa.gfc.JpaGfcSourceFileEntity;
import br.pucminas.graphtest.adapters.outbound.repositories.jpa.interfaces.JpaGfcSourceFileRepository;
import br.pucminas.graphtest.adapters.outbound.repositories.shared.BasePersistenceMapper;
import br.pucminas.graphtest.application.domain.gfc.model.GfcSourceFile;
import br.pucminas.graphtest.application.port.output.repositories.GfcSourceFileRepositoryPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class GfcSourceFileRepositoryAdapter implements GfcSourceFileRepositoryPort {

    private final JpaGfcSourceFileRepository jpaGfcSourceFileRepository;
    private final BasePersistenceMapper<GfcSourceFile, JpaGfcSourceFileEntity> mapper;

    @Override
    public GfcSourceFile save(GfcSourceFile sourceFile) {
        JpaGfcSourceFileEntity entity = mapper.toEntity(sourceFile);
        entity.markAsNew();
        return mapper.toDomain(jpaGfcSourceFileRepository.save(entity));
    }

    @Override
    public Optional<GfcSourceFile> findById(UUID id) {
        return jpaGfcSourceFileRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<GfcSourceFile> findAllByProjectId(UUID projectId) {
        return jpaGfcSourceFileRepository.findAllByProjectIdOrderByCreatedAtDesc(projectId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaGfcSourceFileRepository.deleteById(id);
    }
}
