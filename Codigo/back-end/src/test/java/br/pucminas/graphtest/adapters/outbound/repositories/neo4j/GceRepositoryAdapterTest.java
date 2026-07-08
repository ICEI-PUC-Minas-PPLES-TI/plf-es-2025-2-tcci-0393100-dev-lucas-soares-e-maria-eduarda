package br.pucminas.graphtest.adapters.outbound.repositories.neo4j;

import br.pucminas.graphtest.adapters.outbound.entities.neo4j.gce.Neo4jGceEntity;
import br.pucminas.graphtest.adapters.outbound.repositories.neo4j.interfaces.Neo4jGceRepository;
import br.pucminas.graphtest.adapters.outbound.repositories.neo4j.mapper.GceMapperBase;
import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.domain.gce.model.GceNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GceRepositoryAdapterTest {

    @Mock
    private Neo4jGceRepository neo4jGceRepository;

    private final GceMapperBase mapper = new GceMapperBase();

    private GceRepositoryAdapter adapter;

    private GceRepositoryAdapter adapter() {
        return adapter = new GceRepositoryAdapter(neo4jGceRepository, mapper);
    }

    @Test
    void shouldSaveGraphPreparingAuditBeforePersisting() {
        Gce graph = graph();
        when(neo4jGceRepository.save(org.mockito.ArgumentMatchers.any())).thenAnswer(invocation -> invocation.getArgument(0));

        Gce result = adapter().save(graph);

        assertEquals(graph.getProjectId(), result.getProjectId());
        ArgumentCaptor<Neo4jGceEntity> captor = ArgumentCaptor.forClass(Neo4jGceEntity.class);
        verify(neo4jGceRepository).save(captor.capture());
        assertTrue(captor.getValue().getCreatedAt() != null);
    }

    @Test
    void shouldFindGraphById() {
        UUID graphId = UUID.randomUUID();
        Neo4jGceEntity entity = mapper.toEntity(graph());
        entity.setId(graphId);
        when(neo4jGceRepository.findById(graphId)).thenReturn(Optional.of(entity));

        Optional<Gce> result = adapter().findById(graphId);

        verify(neo4jGceRepository).findById(graphId);
        assertTrue(result.isPresent());
        assertEquals(graphId, result.get().getId());
    }

    @Test
    void shouldReturnEmptyWhenGraphNotFound() {
        UUID graphId = UUID.randomUUID();
        when(neo4jGceRepository.findById(graphId)).thenReturn(Optional.empty());

        assertTrue(adapter().findById(graphId).isEmpty());
    }

    @Test
    void shouldFindAllGraphsByProjectId() {
        UUID projectId = UUID.randomUUID();
        Neo4jGceEntity entity = mapper.toEntity(graph());
        when(neo4jGceRepository.findAllByProjectId(projectId)).thenReturn(List.of(entity));

        List<Gce> result = adapter().findAllByProjectId(projectId);

        verify(neo4jGceRepository).findAllByProjectId(projectId);
        assertEquals(1, result.size());
    }

    @Test
    void shouldDeleteGraphByIdUsingExplicitNeo4jQuery() {
        UUID graphId = UUID.randomUUID();
        doNothing().when(neo4jGceRepository).deleteGraphById(graphId);

        adapter().deleteById(graphId);

        verify(neo4jGceRepository).deleteGraphById(graphId);
    }

    @Test
    void shouldDeleteAllGraphsByProjectIdUsingExplicitNeo4jQuery() {
        UUID projectId = UUID.randomUUID();
        doNothing().when(neo4jGceRepository).deleteAllByProjectId(projectId);

        adapter().deleteAllByProjectId(projectId);

        verify(neo4jGceRepository).deleteAllByProjectId(projectId);
    }

    private Gce graph() {
        return new Gce(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "GCE",
                "Descricao",
                false,
                List.of(
                        GceNode.cause(UUID.randomUUID(), "C1", "Causa 1"),
                        GceNode.effect(UUID.randomUUID(), "E1", "Efeito 1")
                ),
                List.of(),
                List.of()
        );
    }
}
