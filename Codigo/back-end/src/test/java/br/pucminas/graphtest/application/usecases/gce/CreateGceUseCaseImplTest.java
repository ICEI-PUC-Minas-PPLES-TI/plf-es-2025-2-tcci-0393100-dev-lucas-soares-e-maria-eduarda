package br.pucminas.graphtest.application.usecases.gce;

import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.domain.project.model.Project;
import br.pucminas.graphtest.application.domain.gce.enums.GceEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.GceNodeTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.GceOperatorTypeEnum;
import br.pucminas.graphtest.application.port.input.gce.records.CreateGceInput;
import br.pucminas.graphtest.application.port.input.gce.records.GceEdgeInput;
import br.pucminas.graphtest.application.port.input.gce.records.GceNodeInput;
import br.pucminas.graphtest.application.port.input.gce.records.GceOutput;
import br.pucminas.graphtest.application.port.input.gce.records.ValidationGceOutput;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.service.gce.GceMutationServiceImpl;
import br.pucminas.graphtest.application.service.gce.interfaces.GceMutationService;
import br.pucminas.graphtest.application.service.gce.interfaces.GceValidationResultService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateGceUseCaseImplTest {

    @Mock
    private GceRepositoryPort gceRepository;

    @Mock
    private GceValidationResultService gceValidationResultService;

    @Mock
    private ProjectAccessService projectAccessService;

    @Spy
    private GceMutationService gceMutationService = new GceMutationServiceImpl();

    @InjectMocks
    private CreateGceUseCaseImpl useCase;

    @Test
    void shouldCreateSimpleDeterministicGceWithDirectCauseToEffectConnection() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID graphId = UUID.randomUUID();
        CreateGceInput input = new CreateGceInput(
                projectId,
                "GCE simples",
                "Descricao",
                true,
                List.of(
                        new GceNodeInput("C1", "Causa", GceNodeTypeEnum.CAUSE, null),
                        new GceNodeInput("E1", "Efeito", GceNodeTypeEnum.EFFECT, null, List.of("C1"), List.of())
                ),
                List.of(),
                List.of()
        );

        when(projectAccessService.findAuthorizedProject(projectId))
                .thenReturn(new Project(projectId, "Projeto", "Descricao", userId));
        when(gceValidationResultService.validate(any(Gce.class)))
                .thenReturn(new ValidationGceOutput(List.of(), List.of()));
        when(gceRepository.save(any(Gce.class)))
                .thenAnswer(invocation -> {
                    Gce graph = invocation.getArgument(0);
                    return new Gce(
                            graphId,
                            graph.getProjectId(),
                            graph.getName(),
                            graph.getDescription(),
                            graph.isSelected(),
                            graph.getNodes(),
                            graph.getEdges(),
                            graph.getRestrictions()
                    );
                });

        GceOutput output = useCase.execute(input);

        assertEquals(graphId, output.id());
        assertEquals(2, output.nodes().size());
        assertEquals(1, output.edges().size());
        assertEquals("C1", output.edges().getFirst().sourceNodeCode());
        assertEquals("E1", output.edges().getFirst().targetNodeCode());
        assertEquals(GceEdgeTypeEnum.IDENTITY, output.edges().getFirst().type());
    }

    @Test
    void shouldAuthorizeProjectBeforePersistingGce() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID graphId = UUID.randomUUID();
        CreateGceInput input = new CreateGceInput(
                projectId,
                "GCE",
                "Descricao",
                true,
                List.of(
                        new GceNodeInput("C1", "Causa", GceNodeTypeEnum.CAUSE, null),
                        new GceNodeInput("O1", "Operador", GceNodeTypeEnum.OPERATOR, GceOperatorTypeEnum.AND),
                        new GceNodeInput("E1", "Efeito", GceNodeTypeEnum.EFFECT, null)
                ),
                List.of(
                        new GceEdgeInput("C1", "O1", GceEdgeTypeEnum.NEGATED),
                        new GceEdgeInput("O1", "E1", GceEdgeTypeEnum.IDENTITY)
                ),
                List.of()
        );

        when(projectAccessService.findAuthorizedProject(projectId))
                .thenReturn(new Project(projectId, "Projeto", "Descricao", userId));
        when(gceValidationResultService.validate(any(Gce.class)))
                .thenReturn(new ValidationGceOutput(List.of(), List.of()));
        when(gceRepository.save(any(Gce.class)))
                .thenAnswer(invocation -> {
                    Gce graph = invocation.getArgument(0);
                    return new Gce(
                            graphId,
                            graph.getProjectId(),
                            graph.getName(),
                            graph.getDescription(),
                            graph.isSelected(),
                            graph.getNodes(),
                            graph.getEdges(),
                            graph.getRestrictions()
                    );
                });

        GceOutput output = useCase.execute(input);
        ArgumentCaptor<Gce> graphCaptor = ArgumentCaptor.forClass(Gce.class);

        assertEquals(graphId, output.id());
        assertEquals(GceEdgeTypeEnum.NEGATED, output.edges().stream().filter(edge -> edge.sourceNodeCode().equals("C1")).findFirst().orElseThrow().type());
        assertEquals("NOT (C1)", output.nodes().stream().filter(node -> node.code().equals("O1")).findFirst().orElseThrow().label());
        verify(projectAccessService).findAuthorizedProject(projectId);
        verify(gceRepository).save(graphCaptor.capture());
        assertNotNull(graphCaptor.getValue().getCreatedAt());
        assertNull(graphCaptor.getValue().getUpdatedAt());
        assertNull(output.updatedAt());
        assertTrue(graphCaptor.getValue().getNodes().stream().allMatch(node ->
                node.getCreatedAt() != null
                        && node.getUpdatedAt() == null));
        assertTrue(graphCaptor.getValue().getEdges().stream().allMatch(edge ->
                edge.getCreatedAt() != null
                        && edge.getUpdatedAt() == null));
    }

    @Test
    void shouldCreateAutomaticIdentityEdgesWhenOperatorNodeDeclaresConnections() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID graphId = UUID.randomUUID();
        CreateGceInput input = new CreateGceInput(
                projectId,
                "GCE",
                "Descricao",
                true,
                List.of(
                        new GceNodeInput("C1", "Causa 1", GceNodeTypeEnum.CAUSE, null),
                        new GceNodeInput("C2", "Causa 2", GceNodeTypeEnum.CAUSE, null),
                        new GceNodeInput("E1", "Efeito", GceNodeTypeEnum.EFFECT, null),
                        new GceNodeInput("O1", "Operador", GceNodeTypeEnum.OPERATOR, GceOperatorTypeEnum.AND, List.of("C1", "C2"), List.of("E1"))
                ),
                List.of(),
                List.of()
        );

        when(projectAccessService.findAuthorizedProject(projectId))
                .thenReturn(new Project(projectId, "Projeto", "Descricao", userId));
        when(gceValidationResultService.validate(any(Gce.class)))
                .thenReturn(new ValidationGceOutput(List.of(), List.of()));
        when(gceRepository.save(any(Gce.class)))
                .thenAnswer(invocation -> {
                    Gce graph = invocation.getArgument(0);
                    return new Gce(
                            graphId,
                            graph.getProjectId(),
                            graph.getName(),
                            graph.getDescription(),
                            graph.isSelected(),
                            graph.getNodes(),
                            graph.getEdges(),
                            graph.getRestrictions()
                    );
                });

        GceOutput output = useCase.execute(input);

        assertEquals(3, output.edges().size());
        assertTrue(output.edges().stream().allMatch(edge -> edge.type() == GceEdgeTypeEnum.IDENTITY));
        assertEquals("(C1 AND C2)", output.nodes().stream().filter(node -> node.code().equals("O1")).findFirst().orElseThrow().label());
    }

    @Test
    void shouldRejectDuplicateNodeCodeWithinSameGce() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        CreateGceInput input = new CreateGceInput(
                projectId,
                "GCE",
                "Descricao",
                true,
                List.of(
                        new GceNodeInput("C1", "Causa 1", GceNodeTypeEnum.CAUSE, null),
                        new GceNodeInput("C1", "Efeito com codigo duplicado", GceNodeTypeEnum.EFFECT, null)
                ),
                List.of(),
                List.of()
        );

        when(projectAccessService.findAuthorizedProject(projectId))
                .thenReturn(new Project(projectId, "Projeto", "Descricao", userId));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> useCase.execute(input));

        assertTrue(exception.getMessage().contains("duplicado"));
        verify(gceRepository, never()).save(any(Gce.class));
    }
}
