package br.pucminas.graphtest.application.usecases.gce;

import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.domain.gce.model.GceEdge;
import br.pucminas.graphtest.application.domain.gce.model.GceNode;
import br.pucminas.graphtest.application.domain.project.model.Project;
import br.pucminas.graphtest.application.domain.gce.enums.GceEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.GceOperatorTypeEnum;
import br.pucminas.graphtest.application.port.input.gce.records.GceOutput;
import br.pucminas.graphtest.application.port.input.gce.records.ToggleGceEdgeInput;
import br.pucminas.graphtest.application.port.input.gce.records.ValidationGceOutput;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.service.gce.GceMutationServiceImpl;
import br.pucminas.graphtest.application.service.gce.interfaces.GceMutationService;
import br.pucminas.graphtest.application.service.gce.interfaces.GceValidationResultService;
import br.pucminas.graphtest.application.service.decisiontable.interfaces.DecisionTableSyncStatusUpdateService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ToggleGceEdgeUseCaseImplTest {

    @Mock
    private GceRepositoryPort gceRepository;

    @Mock
    private ProjectAccessService projectAccessService;

    @Mock
    private GceValidationResultService gceValidationResultService;

    @Mock
    private DecisionTableSyncStatusUpdateService decisionTableSyncStatusUpdateService;

    @Spy
    private GceMutationService gceMutationService = new GceMutationServiceImpl();

    @InjectMocks
    private ToggleGceEdgeUseCaseImpl useCase;

    @Test
    void shouldToggleEdgeType() {
        UUID graphId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID edgeId = UUID.randomUUID();
        Gce graph = new Gce(
                graphId,
                projectId,
                "GCE",
                "Descricao",
                false,
                List.of(
                        GceNode.cause(UUID.randomUUID(), "C1", "Causa"),
                        GceNode.operator(UUID.randomUUID(), "O1", "Operador", GceOperatorTypeEnum.AND),
                        GceNode.effect(UUID.randomUUID(), "E1", "Efeito")
                ),
                List.of(
                        new GceEdge(edgeId, "C1", "O1", GceEdgeTypeEnum.IDENTITY),
                        new GceEdge(UUID.randomUUID(), "O1", "E1", GceEdgeTypeEnum.IDENTITY)
                ),
                List.of()
        );

        when(gceRepository.findById(graphId)).thenReturn(Optional.of(graph));
        when(projectAccessService.findAuthorizedProject(projectId))
                .thenReturn(new Project(projectId, "Projeto", "Descricao", userId));
        when(gceValidationResultService.validate(any(Gce.class)))
                .thenReturn(new ValidationGceOutput(List.of(), List.of()));
        when(gceRepository.save(any(Gce.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GceOutput output = useCase.execute(new ToggleGceEdgeInput(projectId, graphId, edgeId));

        assertEquals(GceEdgeTypeEnum.NEGATED, output.edges().stream().filter(edge -> edge.id().equals(edgeId)).findFirst().orElseThrow().type());
        verify(gceRepository).save(any(Gce.class));
    }
}
