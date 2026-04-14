package br.pucminas.graphtest.application.usecases.gce;

import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.domain.gce.model.GceNode;
import br.pucminas.graphtest.application.domain.project.model.Project;
import br.pucminas.graphtest.application.domain.gce.enums.GceEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.GceNodeTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.GceOperatorTypeEnum;
import br.pucminas.graphtest.application.port.input.gce.records.AddNodeToGceInput;
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
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddNodeToGceUseCaseImplTest {

    @Mock
    private GceRepositoryPort gceRepository;

    @Mock
    private ProjectAccessService projectAccessService;

    @Mock
    private GceValidationResultService gceValidationResultService;

    @Spy
    private GceMutationService gceMutationService = new GceMutationServiceImpl();

    @InjectMocks
    private AddNodeToGceUseCaseImpl useCase;

    @Test
    void shouldAddOperatorNodeAndCreateIdentityEdgesAutomatically() {
        UUID graphId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Gce graph = new Gce(
                graphId,
                projectId,
                "GCE",
                "Descricao",
                false,
                List.of(
                        GceNode.cause(UUID.randomUUID(), "C1", "Causa 1"),
                        GceNode.cause(UUID.randomUUID(), "C2", "Causa 2"),
                        GceNode.effect(UUID.randomUUID(), "E1", "Efeito 1")
                ),
                List.of(),
                List.of()
        );

        when(gceRepository.findById(graphId)).thenReturn(Optional.of(graph));
        when(projectAccessService.findAuthorizedProject(projectId))
                .thenReturn(new Project(projectId, "Projeto", "Descricao", userId));
        when(gceValidationResultService.validate(any(Gce.class)))
                .thenReturn(new ValidationGceOutput(List.of(), List.of()));
        when(gceRepository.save(any(Gce.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GceOutput output = useCase.execute(new AddNodeToGceInput(
                graphId,
                "O1",
                "Operador",
                GceNodeTypeEnum.OPERATOR,
                GceOperatorTypeEnum.AND,
                List.of("C1", "C2"),
                List.of("E1")
        ));

        assertEquals(4, output.nodes().size());
        assertEquals(3, output.edges().size());
        assertTrue(output.edges().stream().allMatch(edge -> edge.type() == GceEdgeTypeEnum.IDENTITY));
        assertEquals("(C1 AND C2)", output.nodes().stream().filter(node -> node.code().equals("O1")).findFirst().orElseThrow().label());
        verify(gceRepository).save(any(Gce.class));
    }
}
