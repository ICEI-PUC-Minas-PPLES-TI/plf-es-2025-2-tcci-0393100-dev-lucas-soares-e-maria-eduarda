package br.pucminas.graphtest.application.usecases.gce;

import br.pucminas.graphtest.application.domain.gce.enums.GceEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.GceOperatorTypeEnum;
import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.domain.gce.model.GceEdge;
import br.pucminas.graphtest.application.domain.gce.model.GceNode;
import br.pucminas.graphtest.application.domain.project.model.Project;
import br.pucminas.graphtest.application.port.input.gce.records.GceOutput;
import br.pucminas.graphtest.application.port.input.gce.records.ListGcesByProjectInput;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListGcesByProjectUseCaseImplTest {

    @Mock
    private GceRepositoryPort gceRepository;

    @Mock
    private ProjectAccessService projectAccessService;

    @InjectMocks
    private ListGcesByProjectUseCaseImpl useCase;

    @Test
    void shouldAuthorizeProjectBeforeListingGraphs() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Gce graphOne = new Gce(
                UUID.randomUUID(),
                projectId,
                "GCE 1",
                "Descricao 1",
                false,
                List.of(
                        GceNode.cause(UUID.randomUUID(), "C1", "Causa 1"),
                        GceNode.operator(UUID.randomUUID(), "O1", "Operador 1", GceOperatorTypeEnum.AND),
                        GceNode.effect(UUID.randomUUID(), "E1", "Efeito 1")
                ),
                List.of(
                        new GceEdge(UUID.randomUUID(), "C1", "O1", GceEdgeTypeEnum.IDENTITY),
                        new GceEdge(UUID.randomUUID(), "O1", "E1", GceEdgeTypeEnum.IDENTITY)
                ),
                List.of()
        );

        Gce graphTwo = new Gce(
                UUID.randomUUID(),
                projectId,
                "GCE 2",
                "Descricao 2",
                true,
                List.of(
                        GceNode.cause(UUID.randomUUID(), "C2", "Causa 2"),
                        GceNode.operator(UUID.randomUUID(), "O2", "Operador 2", GceOperatorTypeEnum.OR),
                        GceNode.effect(UUID.randomUUID(), "E2", "Efeito 2")
                ),
                List.of(
                        new GceEdge(UUID.randomUUID(), "C2", "O2", GceEdgeTypeEnum.IDENTITY),
                        new GceEdge(UUID.randomUUID(), "O2", "E2", GceEdgeTypeEnum.IDENTITY)
                ),
                List.of()
        );

        when(projectAccessService.findAuthorizedProject(projectId))
                .thenReturn(new Project(projectId, "Projeto", "Descricao", userId));
        when(gceRepository.findAllByProjectId(projectId)).thenReturn(List.of(graphOne, graphTwo));

        List<GceOutput> output = useCase.execute(new ListGcesByProjectInput(projectId));

        assertEquals(2, output.size());
        assertEquals("GCE 1", output.get(0).name());
        assertEquals("GCE 2", output.get(1).name());
        verify(projectAccessService).findAuthorizedProject(projectId);
        verify(gceRepository).findAllByProjectId(projectId);
    }
}
