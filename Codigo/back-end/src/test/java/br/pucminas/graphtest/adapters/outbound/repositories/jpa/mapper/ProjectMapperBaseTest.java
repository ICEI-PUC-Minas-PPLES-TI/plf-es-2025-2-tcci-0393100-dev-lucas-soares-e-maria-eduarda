package br.pucminas.graphtest.adapters.outbound.repositories.jpa.mapper;

import br.pucminas.graphtest.adapters.outbound.entities.jpa.project.JpaProjectEntity;
import br.pucminas.graphtest.adapters.outbound.entities.jpa.user.JpaUserEntity;
import br.pucminas.graphtest.application.domain.project.model.Project;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProjectMapperBaseTest {

    private final ProjectMapperBase mapper = new ProjectMapperBase();

    @Test
    void shouldReturnNullWhenConvertingNullProjectToEntity() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    void shouldReturnNullWhenConvertingNullEntityToDomain() {
        assertNull(mapper.toDomain(null));
    }

    @Test
    void shouldConvertProjectToEntity() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Project project = new Project(projectId, "Projeto", "Descricao", userId, now, now);

        JpaProjectEntity entity = mapper.toEntity(project);

        assertEquals(projectId, entity.getId());
        assertEquals("Projeto", entity.getName());
        assertEquals("Descricao", entity.getDescription());
        assertEquals(userId, entity.getUser().getId());
        assertEquals(now, entity.getCreatedAt());
    }

    @Test
    void shouldConvertEntityWithoutUserToDomainWithNullUserId() {
        JpaProjectEntity entity = new JpaProjectEntity();
        entity.setId(UUID.randomUUID());
        entity.setName("Projeto");
        entity.setDescription("Descricao");
        entity.setUser(null);

        Project project = mapper.toDomain(entity);

        assertNull(project.getUserId());
    }

    @Test
    void shouldConvertEntityToDomain() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        JpaUserEntity user = new JpaUserEntity();
        user.setId(userId);
        JpaProjectEntity entity = new JpaProjectEntity();
        entity.setId(projectId);
        entity.setName("Projeto");
        entity.setDescription("Descricao");
        entity.setUser(user);

        Project project = mapper.toDomain(entity);

        assertEquals(projectId, project.getId());
        assertEquals("Projeto", project.getName());
        assertEquals(userId, project.getUserId());
    }
}
