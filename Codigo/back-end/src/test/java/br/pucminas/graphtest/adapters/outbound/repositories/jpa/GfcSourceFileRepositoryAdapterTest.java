package br.pucminas.graphtest.adapters.outbound.repositories.jpa;

import br.pucminas.graphtest.adapters.outbound.entities.jpa.gfc.JpaGfcSourceFileEntity;
import br.pucminas.graphtest.adapters.outbound.repositories.jpa.interfaces.JpaGfcSourceFileRepository;
import br.pucminas.graphtest.adapters.outbound.repositories.shared.BasePersistenceMapper;
import br.pucminas.graphtest.application.domain.gfc.model.GfcSourceFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GfcSourceFileRepositoryAdapterTest {

    @Mock
    private JpaGfcSourceFileRepository jpaGfcSourceFileRepository;

    @Mock
    private BasePersistenceMapper<GfcSourceFile, JpaGfcSourceFileEntity> mapper;

    @InjectMocks
    private GfcSourceFileRepositoryAdapter adapter;

    @Test
    void shouldFindSourceFileById() {
        UUID sourceFileId = UUID.randomUUID();
        JpaGfcSourceFileEntity entity = new JpaGfcSourceFileEntity();
        GfcSourceFile sourceFile = new GfcSourceFile(
                sourceFileId,
                UUID.randomUUID(),
                "Exemplo.java",
                "class Exemplo {}",
                "Java"
        );
        when(jpaGfcSourceFileRepository.findById(sourceFileId)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(sourceFile);

        Optional<GfcSourceFile> result = adapter.findById(sourceFileId);

        assertTrue(result.isPresent());
        assertEquals(sourceFileId, result.orElseThrow().getId());
        verify(jpaGfcSourceFileRepository).findById(sourceFileId);
    }

    @Test
    void shouldFindAllByProjectIdUsingCreatedAtDescendingRepositoryMethod() {
        UUID projectId = UUID.randomUUID();
        JpaGfcSourceFileEntity entity = new JpaGfcSourceFileEntity();
        GfcSourceFile sourceFile = new GfcSourceFile(
                UUID.randomUUID(),
                projectId,
                "Exemplo.java",
                "class Exemplo {}",
                "Java"
        );
        when(jpaGfcSourceFileRepository.findAllByProjectIdOrderByCreatedAtDesc(projectId)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(sourceFile);

        List<GfcSourceFile> result = adapter.findAllByProjectId(projectId);

        verify(jpaGfcSourceFileRepository).findAllByProjectIdOrderByCreatedAtDesc(projectId);
        assertEquals(1, result.size());
        assertEquals(sourceFile.getId(), result.getFirst().getId());
    }

    @Test
    void shouldDeleteSourceFileById() {
        UUID sourceFileId = UUID.randomUUID();

        adapter.deleteById(sourceFileId);

        verify(jpaGfcSourceFileRepository).deleteById(sourceFileId);
    }

    @Test
    void shouldDeleteAllSourceFilesByProjectId() {
        UUID projectId = UUID.randomUUID();

        adapter.deleteAllByProjectId(projectId);

        verify(jpaGfcSourceFileRepository).deleteAllByProjectId(projectId);
    }
}
