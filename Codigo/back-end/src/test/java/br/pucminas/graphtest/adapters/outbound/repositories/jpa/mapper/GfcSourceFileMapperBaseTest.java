package br.pucminas.graphtest.adapters.outbound.repositories.jpa.mapper;

import br.pucminas.graphtest.adapters.outbound.entities.jpa.gfc.JpaGfcSourceFileEntity;
import br.pucminas.graphtest.adapters.outbound.entities.jpa.project.JpaProjectEntity;
import br.pucminas.graphtest.application.domain.gfc.model.GfcSourceFile;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class GfcSourceFileMapperBaseTest {

    private final GfcSourceFileMapperBase mapper = new GfcSourceFileMapperBase();

    @Test
    void shouldReturnNullWhenConvertingNullSourceFileToEntity() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    void shouldReturnNullWhenConvertingNullEntityToDomain() {
        assertNull(mapper.toDomain(null));
    }

    @Test
    void shouldConvertSourceFileToEntity() {
        UUID id = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        GfcSourceFile sourceFile = new GfcSourceFile(id, projectId, "Teste.java", "class Teste {}", "java");

        JpaGfcSourceFileEntity entity = mapper.toEntity(sourceFile);

        assertEquals(id, entity.getId());
        assertEquals(projectId, entity.getProject().getId());
        assertEquals("Teste.java", entity.getFileName());
        assertEquals("class Teste {}", entity.getContent());
        assertEquals("Java", entity.getLanguage());
    }

    @Test
    void shouldConvertEntityToSourceFile() {
        UUID id = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        JpaProjectEntity project = new JpaProjectEntity();
        project.setId(projectId);
        JpaGfcSourceFileEntity entity = new JpaGfcSourceFileEntity();
        entity.setId(id);
        entity.setProject(project);
        entity.setFileName("Teste.java");
        entity.setContent("class Teste {}");
        entity.setLanguage("java");

        GfcSourceFile sourceFile = mapper.toDomain(entity);

        assertEquals(id, sourceFile.getId());
        assertEquals(projectId, sourceFile.getProjectId());
        assertEquals("Teste.java", sourceFile.getFileName());
    }
}
