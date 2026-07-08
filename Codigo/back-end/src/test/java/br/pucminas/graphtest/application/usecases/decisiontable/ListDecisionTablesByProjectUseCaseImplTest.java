package br.pucminas.graphtest.application.usecases.decisiontable;

import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableSyncStatusEnum;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTable;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableOutput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.ListDecisionTablesByProjectInput;
import br.pucminas.graphtest.application.port.output.repositories.DecisionTableRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListDecisionTablesByProjectUseCaseImplTest {

    @Mock
    private DecisionTableRepositoryPort decisionTableRepository;

    @Mock
    private ProjectAccessService projectAccessService;

    @InjectMocks
    private ListDecisionTablesByProjectUseCaseImpl useCase;

    @Test
    void shouldAuthorizeProjectAndListDecisionTables() {
        UUID projectId = UUID.randomUUID();
        DecisionTable table = decisionTable(projectId);
        when(decisionTableRepository.findAllByProjectId(projectId)).thenReturn(List.of(table));

        List<DecisionTableOutput> outputs = useCase.execute(new ListDecisionTablesByProjectInput(projectId));

        assertEquals(1, outputs.size());
        assertEquals(table.getId(), outputs.getFirst().id());

        InOrder inOrder = inOrder(projectAccessService, decisionTableRepository);
        inOrder.verify(projectAccessService).findAuthorizedProject(projectId);
        inOrder.verify(decisionTableRepository).findAllByProjectId(projectId);
    }

    @Test
    void shouldReturnEmptyListWhenProjectHasNoDecisionTables() {
        UUID projectId = UUID.randomUUID();
        when(decisionTableRepository.findAllByProjectId(projectId)).thenReturn(List.of());

        List<DecisionTableOutput> outputs = useCase.execute(new ListDecisionTablesByProjectInput(projectId));

        assertEquals(0, outputs.size());
    }

    private DecisionTable decisionTable(UUID projectId) {
        return new DecisionTable(
                UUID.randomUUID(),
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
