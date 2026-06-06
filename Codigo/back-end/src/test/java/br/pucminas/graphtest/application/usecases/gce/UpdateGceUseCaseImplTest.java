package br.pucminas.graphtest.application.usecases.gce;

import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.domain.gce.model.GceEdge;
import br.pucminas.graphtest.application.domain.gce.model.GceNode;
import br.pucminas.graphtest.application.domain.project.model.Project;
import br.pucminas.graphtest.application.domain.gce.enums.GceEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.GceNodeTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.GceOperatorTypeEnum;
import br.pucminas.graphtest.application.port.input.gce.records.GceEdgeInput;
import br.pucminas.graphtest.application.port.input.gce.records.GceNodeInput;
import br.pucminas.graphtest.application.port.input.gce.records.GceOutput;
import br.pucminas.graphtest.application.port.input.gce.records.UpdateGceInput;
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
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateGceUseCaseImplTest {

    @Mock
    private GceRepositoryPort gceRepository;

    @Mock
    private ProjectAccessService projectAccessService;

    @Mock
    private GceValidationResultService gceValidationResultService;

    @Spy
    private GceMutationService gceMutationService = new GceMutationServiceImpl();

    @Mock
    private DecisionTableSyncStatusUpdateService decisionTableSyncStatusUpdateService;

    @InjectMocks
    private UpdateGceUseCaseImpl useCase;

    @Test
    void shouldReplaceWholeGraphAndKeepExplicitNegatedEdges() {
        UUID graphId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID causeOneId = UUID.randomUUID();
        UUID operatorId = UUID.randomUUID();
        UUID effectId = UUID.randomUUID();
        UUID causeToOperatorEdgeId = UUID.randomUUID();
        UUID operatorToEffectEdgeId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        Gce graph = new Gce(
                graphId,
                projectId,
                "Nome antigo",
                "Descricao antiga",
                false,
                List.of(
                        GceNode.cause(causeOneId, "C1", "Causa", createdAt, null),
                        GceNode.operator(operatorId, "O1", "Operador", GceOperatorTypeEnum.AND, createdAt, null),
                        GceNode.effect(effectId, "E1", "Efeito", createdAt, null)
                ),
                List.of(
                        new GceEdge(causeToOperatorEdgeId, "C1", "O1", GceEdgeTypeEnum.IDENTITY, createdAt, null),
                        new GceEdge(operatorToEffectEdgeId, "O1", "E1", GceEdgeTypeEnum.IDENTITY, createdAt, null)
                ),
                List.of(),
                createdAt,
                null
        );

        when(gceRepository.findById(graphId)).thenReturn(Optional.of(graph));
        when(projectAccessService.findAuthorizedProject(projectId))
                .thenReturn(new Project(projectId, "Projeto", "Descricao", userId));
        when(gceValidationResultService.validate(any(Gce.class)))
                .thenReturn(new br.pucminas.graphtest.application.port.input.gce.records.ValidationGceOutput(List.of(), List.of()));
        when(decisionTableSyncStatusUpdateService.hasDecisionTableRelevantChanges(any(Gce.class), any(Gce.class)))
                .thenReturn(false);
        when(gceRepository.save(any(Gce.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GceOutput output = useCase.execute(new UpdateGceInput(
                graphId,
                projectId,
                "Nome novo",
                "Descricao nova",
                true,
                List.of(
                        new GceNodeInput("C1", "Causa 1", GceNodeTypeEnum.CAUSE, null),
                        new GceNodeInput("C2", "Causa 2", GceNodeTypeEnum.CAUSE, null),
                        new GceNodeInput("O1", "Operador OR", GceNodeTypeEnum.OPERATOR, GceOperatorTypeEnum.OR),
                        new GceNodeInput("E1", "Efeito final", GceNodeTypeEnum.EFFECT, null)
                ),
                List.of(
                        new GceEdgeInput("C1", "O1", GceEdgeTypeEnum.NEGATED),
                        new GceEdgeInput("C2", "O1", GceEdgeTypeEnum.IDENTITY),
                        new GceEdgeInput("O1", "E1", GceEdgeTypeEnum.IDENTITY)
                ),
                List.of()
        ));

        assertEquals("Nome novo", output.name());
        assertEquals("Descricao nova", output.description());
        assertEquals(true, output.selected());
        assertEquals(4, output.nodes().size());
        assertEquals(GceEdgeTypeEnum.NEGATED, output.edges().stream().filter(edge -> edge.sourceNodeCode().equals("C1")).findFirst().orElseThrow().type());

        ArgumentCaptor<Gce> graphCaptor = ArgumentCaptor.forClass(Gce.class);
        verify(gceRepository).save(graphCaptor.capture());

        Gce savedGraph = graphCaptor.getValue();
        GceNode persistedCause = savedGraph.findNode("C1").orElseThrow();
        assertEquals(causeOneId, persistedCause.getId());
        assertEquals(createdAt, persistedCause.getCreatedAt());
        assertNotNull(persistedCause.getUpdatedAt());

        GceEdge persistedChangedEdge = savedGraph.getEdges().stream()
                .filter(edge -> edge.getSourceNodeCode().equals("C1") && edge.getTargetNodeCode().equals("O1"))
                .findFirst()
                .orElseThrow();
        assertEquals(causeToOperatorEdgeId, persistedChangedEdge.getId());
        assertEquals(createdAt, persistedChangedEdge.getCreatedAt());
        assertNotNull(persistedChangedEdge.getUpdatedAt());
    }
}
