package br.pucminas.graphtest.adapters.outbound.repositories.jpa.mapper;

import br.pucminas.graphtest.adapters.outbound.entities.jpa.gfc.JpaGfcSourceFileEntity;
import br.pucminas.graphtest.adapters.outbound.repositories.shared.BasePersistenceMapper;
import br.pucminas.graphtest.application.domain.gfc.model.GfcSourceFile;
import org.springframework.stereotype.Component;

@Component
public class GfcSourceFileMapperBase implements BasePersistenceMapper<GfcSourceFile, JpaGfcSourceFileEntity> {

    @Override
    public JpaGfcSourceFileEntity toEntity(GfcSourceFile sourceFile) {
        if (sourceFile == null) {
            return null;
        }

        JpaGfcSourceFileEntity entity = new JpaGfcSourceFileEntity();
        entity.setId(sourceFile.getId());
        entity.setCreatedAt(sourceFile.getCreatedAt());
        entity.setUpdatedAt(sourceFile.getUpdatedAt());
        entity.setProjectId(sourceFile.getProjectId());
        entity.setFileName(sourceFile.getFileName());
        entity.setContent(sourceFile.getContent());
        entity.setLanguage(sourceFile.getLanguage());
        return entity;
    }

    @Override
    public GfcSourceFile toDomain(JpaGfcSourceFileEntity entity) {
        if (entity == null) {
            return null;
        }

        return new GfcSourceFile(
                entity.getId(),
                entity.getProjectId(),
                entity.getFileName(),
                entity.getContent(),
                entity.getLanguage(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
