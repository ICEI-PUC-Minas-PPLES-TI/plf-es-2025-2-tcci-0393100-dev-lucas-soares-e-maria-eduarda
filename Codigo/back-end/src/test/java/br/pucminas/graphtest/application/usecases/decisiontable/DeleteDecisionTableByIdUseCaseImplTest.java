package br.pucminas.graphtest.application.usecases.decisiontable;

import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableSyncStatusEnum;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTable;
import br.pucminas.graphtest.application.domain.project.model.Project;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableByIdInput;
import br.pucminas.graphtest.application.port.output.repositories.DecisionTableRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteDecisionTableByIdUseCaseImplTest {

    private static final UUID TABLE_ID = UUID.randomUUID();
    private static final UUID PROJECT_ID = UUID.randomUUID();

    @Mock
    private DecisionTableRepositoryPort decisionTableRepository;

    @Mock
    private ProjectAccessService projectAccessService;

    @InjectMocks
    private DeleteDecisionTableByIdUseCaseImpl useCase;

    @Test
    void shouldAuthorizeProjectAndDeleteDecisionTable() {
        DecisionTable table = decisionTable(TABLE_ID, PROJECT_ID);
        when(decisionTableRepository.findById(TABLE_ID)).thenReturn(Optional.of(table));
        when(projectAccessService.findAuthorizedProject(PROJECT_ID))
                .thenReturn(new Project(PROJECT_ID, "Projeto", "Descricao", UUID.randomUUID()));

        useCase.execute(new DecisionTableByIdInput(PROJECT_ID, TABLE_ID));

        verify(projectAccessService).findAuthorizedProject(PROJECT_ID);
        verify(decisionTableRepository).deleteById(TABLE_ID);
    }

    @Test
    void shouldThrowNotFoundWhenDecisionTableDoesNotExist() {
        when(decisionTableRepository.findById(TABLE_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> useCase.execute(new DecisionTableByIdInput(PROJECT_ID, TABLE_ID)));
        verifyNoInteractions(projectAccessService);
        verify(decisionTableRepository, never()).deleteById(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldThrowNotFoundWhenDecisionTableDoesNotBelongToProject() {
        UUID actualProjectId = UUID.randomUUID();
        DecisionTable table = decisionTable(TABLE_ID, actualProjectId);
        when(decisionTableRepository.findById(TABLE_ID)).thenReturn(Optional.of(table));
        when(projectAccessService.findAuthorizedProject(actualProjectId))
                .thenReturn(new Project(actualProjectId, "Projeto", "Descricao", UUID.randomUUID()));

        assertThrows(EntityNotFoundException.class,
                () -> useCase.execute(new DecisionTableByIdInput(PROJECT_ID, TABLE_ID)));
        verify(decisionTableRepository, never()).deleteById(org.mockito.ArgumentMatchers.any());
    }

    private DecisionTable decisionTable(UUID id, UUID projectId) {
        return new DecisionTable(
                id,
                UUID.randomUUID(),
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
}
