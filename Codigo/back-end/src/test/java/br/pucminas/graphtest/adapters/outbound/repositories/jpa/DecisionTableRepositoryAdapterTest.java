package br.pucminas.graphtest.adapters.outbound.repositories.jpa;

import br.pucminas.graphtest.adapters.outbound.entities.jpa.decisiontable.JpaDecisionTableEntity;
import br.pucminas.graphtest.adapters.outbound.repositories.jpa.interfaces.JpaDecisionTableRepository;
import br.pucminas.graphtest.adapters.outbound.repositories.shared.BasePersistenceMapper;
import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableSyncStatusEnum;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DecisionTableRepositoryAdapterTest {

    @Mock
    private JpaDecisionTableRepository jpaDecisionTableRepository;

    @Mock
    private BasePersistenceMapper<DecisionTable, JpaDecisionTableEntity> mapper;

    @InjectMocks
    private DecisionTableRepositoryAdapter adapter;

    @Test
    void shouldDeleteAndReinsertWhenDecisionTableAlreadyExists() {
        UUID tableId = UUID.randomUUID();
        JpaDecisionTableEntity entity = new JpaDecisionTableEntity();
        entity.setId(tableId);
        DecisionTable table = decisionTable(tableId);
        when(mapper.toEntity(table)).thenReturn(entity);
        when(jpaDecisionTableRepository.existsById(tableId)).thenReturn(true);
        when(jpaDecisionTableRepository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(table);

        DecisionTable result = adapter.save(table);

        assertEquals(table, result);
        verify(jpaDecisionTableRepository).deleteById(tableId);
        verify(jpaDecisionTableRepository).flush();
        ArgumentCaptor<JpaDecisionTableEntity> captor = ArgumentCaptor.forClass(JpaDecisionTableEntity.class);
        verify(jpaDecisionTableRepository).save(captor.capture());
        assertTrue(captor.getValue().isNew());
    }

    @Test
    void shouldSaveWithoutDeletingWhenDecisionTableDoesNotExistYet() {
        UUID tableId = UUID.randomUUID();
        JpaDecisionTableEntity entity = new JpaDecisionTableEntity();
        entity.setId(tableId);
        DecisionTable table = decisionTable(tableId);
        when(mapper.toEntity(table)).thenReturn(entity);
        when(jpaDecisionTableRepository.existsById(tableId)).thenReturn(false);
        when(jpaDecisionTableRepository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(table);

        adapter.save(table);

        verify(jpaDecisionTableRepository, never()).deleteById(tableId);
        verify(jpaDecisionTableRepository, never()).flush();
        verify(jpaDecisionTableRepository, times(1)).save(entity);
    }

    @Test
    void shouldFindDecisionTableById() {
        UUID tableId = UUID.randomUUID();
        JpaDecisionTableEntity entity = new JpaDecisionTableEntity();
        when(jpaDecisionTableRepository.findById(tableId)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(decisionTable(tableId));

        assertTrue(adapter.findById(tableId).isPresent());
    }

    @Test
    void shouldFindDecisionTableByGceId() {
        UUID gceId = UUID.randomUUID();
        JpaDecisionTableEntity entity = new JpaDecisionTableEntity();
        when(jpaDecisionTableRepository.findByGceId(gceId)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(decisionTable(UUID.randomUUID()));

        assertTrue(adapter.findByGceId(gceId).isPresent());
    }

    @Test
    void shouldFindAllDecisionTablesByProjectId() {
        UUID projectId = UUID.randomUUID();
        JpaDecisionTableEntity entity = new JpaDecisionTableEntity();
        when(jpaDecisionTableRepository.findAllByProject_Id(projectId)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(decisionTable(UUID.randomUUID()));

        assertEquals(1, adapter.findAllByProjectId(projectId).size());
    }

    @Test
    void shouldDeleteAllDecisionTablesByProjectId() {
        UUID projectId = UUID.randomUUID();

        adapter.deleteAllByProjectId(projectId);

        verify(jpaDecisionTableRepository).deleteAllByProject_Id(projectId);
    }

    @Test
    void shouldDeleteDecisionTableById() {
        UUID tableId = UUID.randomUUID();

        adapter.deleteById(tableId);

        verify(jpaDecisionTableRepository).deleteById(tableId);
    }

    @Test
    void shouldDeleteDecisionTableByGceId() {
        UUID gceId = UUID.randomUUID();

        adapter.deleteByGceId(gceId);

        verify(jpaDecisionTableRepository).deleteByGceId(gceId);
    }

    @Test
    void shouldCheckExistenceByGceId() {
        UUID gceId = UUID.randomUUID();
        when(jpaDecisionTableRepository.existsByGceId(gceId)).thenReturn(true);

        assertTrue(adapter.existsByGceId(gceId));
    }

    @Test
    void shouldReturnFalseWhenNoDecisionTableExistsForGceId() {
        UUID gceId = UUID.randomUUID();
        when(jpaDecisionTableRepository.existsByGceId(gceId)).thenReturn(false);

        assertFalse(adapter.existsByGceId(gceId));
    }

    private DecisionTable decisionTable(UUID id) {
        return new DecisionTable(
                id,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Tabela",
                null,
                "fingerprint",
                DecisionTableSyncStatusEnum.UP_TO_DATE,
                null,
                List.of(),
                List.of()
        );
    }
}
