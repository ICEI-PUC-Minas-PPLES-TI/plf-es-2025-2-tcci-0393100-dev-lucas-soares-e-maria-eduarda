package br.pucminas.graphtest.application.usecases.gce;

import br.pucminas.graphtest.application.domain.gce.enums.GceEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.GceOperatorTypeEnum;
import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.domain.gce.model.GceEdge;
import br.pucminas.graphtest.application.domain.gce.model.GceNode;
import br.pucminas.graphtest.application.port.input.gce.records.DeleteGceInput;
import br.pucminas.graphtest.application.port.output.repositories.DecisionTableRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.service.gce.GceMutationServiceImpl;
import br.pucminas.graphtest.application.service.gce.interfaces.GceMutationService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteGceUseCaseImplTest {

    @Mock
    private GceRepositoryPort gceRepository;

    @Mock
    private ProjectAccessService projectAccessService;

    @Mock
    private DecisionTableRepositoryPort decisionTableRepository;

    @Spy
    private GceMutationService gceMutationService = new GceMutationServiceImpl();

    @InjectMocks
    private DeleteGceUseCaseImpl useCase;

    @Test
    void shouldAuthorizeAndDeleteGceById() {
        UUID graphId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        Gce graph = new Gce(
                graphId,
                projectId,
                "GCE",
                "Descricao",
                false,
                List.of(
                        GceNode.cause(UUID.randomUUID(), "C1", "Causa 1"),
                        GceNode.cause(UUID.randomUUID(), "C2", "Causa 2"),
                        GceNode.operator(UUID.randomUUID(), "O1", "Operador", GceOperatorTypeEnum.AND),
                        GceNode.effect(UUID.randomUUID(), "E1", "Efeito")
                ),
                List.of(
                        new GceEdge(UUID.randomUUID(), "C1", "O1", GceEdgeTypeEnum.IDENTITY),
                        new GceEdge(UUID.randomUUID(), "C2", "O1", GceEdgeTypeEnum.IDENTITY),
                        new GceEdge(UUID.randomUUID(), "O1", "E1", GceEdgeTypeEnum.IDENTITY)
                ),
                List.of()
        );

        when(gceRepository.findById(graphId)).thenReturn(Optional.of(graph));
        when(decisionTableRepository.findByGceId(graphId)).thenReturn(Optional.empty());

        useCase.execute(new DeleteGceInput(projectId, graphId));

        InOrder inOrder = inOrder(gceRepository, projectAccessService, decisionTableRepository);
        inOrder.verify(gceRepository).findById(graphId);
        inOrder.verify(projectAccessService).findAuthorizedProject(projectId);
        inOrder.verify(decisionTableRepository).findByGceId(graphId);
        inOrder.verify(gceRepository).deleteById(graphId);
        verify(gceRepository).deleteById(graphId);
    }
}
