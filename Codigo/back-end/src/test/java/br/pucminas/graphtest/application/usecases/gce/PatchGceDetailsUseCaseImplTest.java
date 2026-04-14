package br.pucminas.graphtest.application.usecases.gce;

import br.pucminas.graphtest.application.domain.gce.enums.GceEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.GceOperatorTypeEnum;
import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.domain.gce.model.GceEdge;
import br.pucminas.graphtest.application.domain.gce.model.GceNode;
import br.pucminas.graphtest.application.domain.project.model.Project;
import br.pucminas.graphtest.application.port.input.gce.records.GceOutput;
import br.pucminas.graphtest.application.port.input.gce.records.UpdateGceDetailsInput;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.service.gce.GceMutationServiceImpl;
import br.pucminas.graphtest.application.service.gce.interfaces.GceMutationService;
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
class PatchGceDetailsUseCaseImplTest {

    @Mock
    private GceRepositoryPort gceRepository;

    @Mock
    private ProjectAccessService projectAccessService;

    @Spy
    private GceMutationService gceMutationService = new GceMutationServiceImpl();

    @InjectMocks
    private PatchGceDetailsUseCaseImpl useCase;

    @Test
    void shouldUpdateOnlyNameAndDescription() {
        UUID graphId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Gce graph = new Gce(
                graphId,
                projectId,
                "Nome antigo",
                "Descricao antiga",
                false,
                List.of(
                        GceNode.cause(UUID.randomUUID(), "C1", "Causa"),
                        GceNode.operator(UUID.randomUUID(), "O1", "(C1 AND C2)", GceOperatorTypeEnum.AND),
                        GceNode.effect(UUID.randomUUID(), "E1", "Efeito")
                ),
                List.of(
                        new GceEdge(UUID.randomUUID(), "C1", "O1", GceEdgeTypeEnum.IDENTITY),
                        new GceEdge(UUID.randomUUID(), "O1", "E1", GceEdgeTypeEnum.IDENTITY)
                ),
                List.of()
        );

        when(gceRepository.findById(graphId)).thenReturn(Optional.of(graph));
        when(projectAccessService.findAuthorizedProject(projectId))
                .thenReturn(new Project(projectId, "Projeto", "Descricao", userId));
        when(gceRepository.save(any(Gce.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GceOutput output = useCase.execute(new UpdateGceDetailsInput(graphId, "Nome novo", "Descricao nova"));

        assertEquals("Nome novo", output.name());
        assertEquals("Descricao nova", output.description());
        assertEquals(3, output.nodes().size());
        assertEquals(2, output.edges().size());
        verify(gceRepository).save(any(Gce.class));
    }
}
