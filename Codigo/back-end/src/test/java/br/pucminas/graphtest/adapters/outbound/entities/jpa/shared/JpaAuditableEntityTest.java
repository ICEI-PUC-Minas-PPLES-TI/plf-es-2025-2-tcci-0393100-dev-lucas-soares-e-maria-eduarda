package br.pucminas.graphtest.adapters.outbound.entities.jpa.shared;

import br.pucminas.graphtest.adapters.outbound.entities.jpa.gfc.JpaGfcSourceFileEntity;
import br.pucminas.graphtest.adapters.outbound.entities.jpa.project.JpaProjectEntity;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JpaAuditableEntityTest {

    @Test
    void shouldFillCreatedAtAndUpdatedAtWhenCreatingProject() {
        JpaProjectEntity project = new JpaProjectEntity();

        invokeLifecycle(project, "onCreate");

        assertNotNull(project.getCreatedAt());
        assertNotNull(project.getUpdatedAt());
        assertEquals(project.getCreatedAt(), project.getUpdatedAt());
    }

    @Test
    void shouldPreserveCreatedAtAndRefreshUpdatedAtWhenUpdatingProject() {
        JpaProjectEntity project = new JpaProjectEntity();
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime previousUpdatedAt = createdAt.plusHours(1);
        project.setCreatedAt(createdAt);
        project.setUpdatedAt(previousUpdatedAt);

        invokeLifecycle(project, "onUpdate");

        assertEquals(createdAt, project.getCreatedAt());
        assertTrue(project.getUpdatedAt().isAfter(previousUpdatedAt));
    }

    @Test
    void shouldFillCreatedAtAndUpdatedAtWhenCreatingAdditionalRelationalEntity() {
        JpaGfcSourceFileEntity sourceFile = new JpaGfcSourceFileEntity();

        invokeLifecycle(sourceFile, "onCreate");

        assertNotNull(sourceFile.getCreatedAt());
        assertNotNull(sourceFile.getUpdatedAt());
        assertEquals(sourceFile.getCreatedAt(), sourceFile.getUpdatedAt());
    }

    private void invokeLifecycle(JpaAuditableEntity entity, String methodName) {
        try {
            Method method = JpaAuditableEntity.class.getDeclaredMethod(methodName);
            method.setAccessible(true);
            method.invoke(entity);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            throw new AssertionError("Falha ao executar callback de auditoria JPA", exception);
        }
    }
}
