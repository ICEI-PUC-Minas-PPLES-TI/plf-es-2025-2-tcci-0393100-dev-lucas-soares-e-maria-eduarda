package br.pucminas.graphtest.adapters.outbound.repositories.jpa;

import br.pucminas.graphtest.adapters.outbound.entities.jpa.project.JpaProjectEntity;
import br.pucminas.graphtest.adapters.outbound.repositories.jpa.interfaces.JpaProjectRepository;
import br.pucminas.graphtest.adapters.outbound.repositories.shared.BasePersistenceMapper;
import br.pucminas.graphtest.application.domain.project.model.Project;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectRepositoryAdapterTest {

    @Mock
    private JpaProjectRepository jpaProjectRepository;

    @Mock
    private BasePersistenceMapper<Project, JpaProjectEntity> mapper;

    @InjectMocks
    private ProjectRepositoryAdapter adapter;

    @Test
    void shouldSaveProject() {
        Project project = project();
        JpaProjectEntity entity = new JpaProjectEntity();
        when(mapper.toEntity(project)).thenReturn(entity);
        when(jpaProjectRepository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(project);

        assertEquals(project, adapter.save(project));
    }

    @Test
    void shouldFindProjectById() {
        UUID projectId = UUID.randomUUID();
        JpaProjectEntity entity = new JpaProjectEntity();
        when(jpaProjectRepository.findById(projectId)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(project());

        assertTrue(adapter.findById(projectId).isPresent());
    }

    @Test
    void shouldFindAllProjects() {
        JpaProjectEntity entity = new JpaProjectEntity();
        when(jpaProjectRepository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(project());

        assertEquals(1, adapter.findAll().size());
    }

    @Test
    void shouldFindAllProjectsByUserId() {
        UUID userId = UUID.randomUUID();
        JpaProjectEntity entity = new JpaProjectEntity();
        when(jpaProjectRepository.findAllByUserId(userId)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(project());

        assertEquals(1, adapter.findAllByUserId(userId).size());
    }

    @Test
    void shouldFindProjectByIdAndUserId() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        JpaProjectEntity entity = new JpaProjectEntity();
        when(jpaProjectRepository.findByIdAndUserId(projectId, userId)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(project());

        assertTrue(adapter.findByIdAndUserId(projectId, userId).isPresent());
    }

    @Test
    void shouldDeleteProjectById() {
        UUID projectId = UUID.randomUUID();

        adapter.deleteById(projectId);

        verify(jpaProjectRepository).deleteById(projectId);
    }

    @Test
    void shouldCountProjectsByUserId() {
        UUID userId = UUID.randomUUID();
        when(jpaProjectRepository.countByUserId(userId)).thenReturn(3L);

        assertEquals(3L, adapter.countByUserId(userId));
    }

    @Test
    void shouldDeleteAllProjectsByUserId() {
        UUID userId = UUID.randomUUID();

        adapter.deleteAllByUserId(userId);

        verify(jpaProjectRepository).deleteAllByUser_Id(userId);
    }

    private Project project() {
        return new Project(UUID.randomUUID(), "Projeto", "Descricao", UUID.randomUUID());
    }
}
