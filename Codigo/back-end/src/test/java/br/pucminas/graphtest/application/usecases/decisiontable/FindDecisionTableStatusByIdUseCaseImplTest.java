package br.pucminas.graphtest.application.usecases.decisiontable;

import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableSyncStatusEnum;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTable;
import br.pucminas.graphtest.application.domain.gce.enums.GceEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.domain.gce.model.GceEdge;
import br.pucminas.graphtest.application.domain.gce.model.GceNode;
import br.pucminas.graphtest.application.domain.project.model.Project;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableByIdInput;
import br.pucminas.graphtest.application.port.output.repositories.DecisionTableRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.service.decisiontable.interfaces.DecisionTableSyncService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindDecisionTableStatusByIdUseCaseImplTest {

    private static final UUID TABLE_ID = UUID.randomUUID();
    private static final UUID PROJECT_ID = UUID.randomUUID();
    private static final UUID GCE_ID = UUID.randomUUID();

    @Mock
    private DecisionTableRepositoryPort decisionTableRepository;

    @Mock
    private GceRepositoryPort gceRepository;

    @Mock
    private ProjectAccessService projectAccessService;

    @Mock
    private DecisionTableSyncService decisionTableSyncService;

    @InjectMocks
    private FindDecisionTableStatusByIdUseCaseImpl useCase;

    @Test
    void shouldReturnTrueWhenGraphExistsAndIsNotStale() {
        DecisionTable table = decisionTable(GCE_ID);
        Gce graph = graph();
        when(decisionTableRepository.findById(TABLE_ID)).thenReturn(Optional.of(table));
        authorize();
        when(gceRepository.findById(GCE_ID)).thenReturn(Optional.of(graph));
        when(decisionTableSyncService.isStale(table, graph)).thenReturn(false);

        assertTrue(useCase.execute(new DecisionTableByIdInput(PROJECT_ID, TABLE_ID)));
    }

    @Test
    void shouldReturnFalseWhenGraphIsStale() {
        DecisionTable table = decisionTable(GCE_ID);
        Gce graph = graph();
        when(decisionTableRepository.findById(TABLE_ID)).thenReturn(Optional.of(table));
        authorize();
        when(gceRepository.findById(GCE_ID)).thenReturn(Optional.of(graph));
        when(decisionTableSyncService.isStale(table, graph)).thenReturn(true);

        assertFalse(useCase.execute(new DecisionTableByIdInput(PROJECT_ID, TABLE_ID)));
    }

    @Test
    void shouldReturnFalseWhenDecisionTableHasNoGceReference() {
        DecisionTable table = decisionTable(null);
        when(decisionTableRepository.findById(TABLE_ID)).thenReturn(Optional.of(table));
        authorize();

        assertFalse(useCase.execute(new DecisionTableByIdInput(PROJECT_ID, TABLE_ID)));
        verify(gceRepository, never()).findById(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldReturnFalseWhenGceNoLongerExists() {
        DecisionTable table = decisionTable(GCE_ID);
        when(decisionTableRepository.findById(TABLE_ID)).thenReturn(Optional.of(table));
        authorize();
        when(gceRepository.findById(GCE_ID)).thenReturn(Optional.empty());

        assertFalse(useCase.execute(new DecisionTableByIdInput(PROJECT_ID, TABLE_ID)));
    }

    @Test
    void shouldThrowNotFoundWhenDecisionTableDoesNotExist() {
        when(decisionTableRepository.findById(TABLE_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> useCase.execute(new DecisionTableByIdInput(PROJECT_ID, TABLE_ID)));
        verifyNoInteractions(projectAccessService, gceRepository);
    }

    @Test
    void shouldThrowNotFoundWhenDecisionTableDoesNotBelongToProject() {
        UUID actualProjectId = UUID.randomUUID();
        DecisionTable table = decisionTable(GCE_ID, actualProjectId);
        when(decisionTableRepository.findById(TABLE_ID)).thenReturn(Optional.of(table));
        when(projectAccessService.findAuthorizedProject(actualProjectId))
                .thenReturn(new Project(actualProjectId, "Projeto", "Descricao", UUID.randomUUID()));

        assertThrows(EntityNotFoundException.class,
                () -> useCase.execute(new DecisionTableByIdInput(PROJECT_ID, TABLE_ID)));
    }

    private void authorize() {
        when(projectAccessService.findAuthorizedProject(PROJECT_ID))
                .thenReturn(new Project(PROJECT_ID, "Projeto", "Descricao", UUID.randomUUID()));
    }

    private DecisionTable decisionTable(UUID gceId) {
        return decisionTable(gceId, PROJECT_ID);
    }

    private DecisionTable decisionTable(UUID gceId, UUID projectId) {
        return new DecisionTable(
                TABLE_ID,
                gceId,
                projectId,
                "Tabela",
                null,
                "fingerprint",
                DecisionTableSyncStatusEnum.UP_TO_DATE,
                null,
                List.of(),
                List.of()
        );
    }

    private Gce graph() {
        return new Gce(
                GCE_ID,
                PROJECT_ID,
                "GCE",
                null,
                false,
                List.of(GceNode.cause(UUID.randomUUID(), "C1", "Causa"), GceNode.effect(UUID.randomUUID(), "E1", "Efeito")),
                List.of(new GceEdge(UUID.randomUUID(), "C1", "E1", GceEdgeTypeEnum.IDENTITY)),
                List.of()
        );
    }
}
