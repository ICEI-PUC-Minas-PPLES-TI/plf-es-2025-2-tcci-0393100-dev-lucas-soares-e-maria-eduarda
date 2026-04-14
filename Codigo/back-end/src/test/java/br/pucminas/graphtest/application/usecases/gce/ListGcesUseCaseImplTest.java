package br.pucminas.graphtest.application.usecases.gce;

import br.pucminas.graphtest.application.domain.gce.enums.GceEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.GceOperatorTypeEnum;
import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.domain.gce.model.GceEdge;
import br.pucminas.graphtest.application.domain.gce.model.GceNode;
import br.pucminas.graphtest.application.domain.project.model.Project;
import br.pucminas.graphtest.application.domain.user.enums.UserProfileEnum;
import br.pucminas.graphtest.application.port.input.gce.records.GceOutput;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepositoryPort;
import br.pucminas.graphtest.application.port.output.security.CurrentUserPort;
import br.pucminas.graphtest.application.security.AuthenticatedUser;
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
class ListGcesUseCaseImplTest {

    @Mock
    private GceRepositoryPort gceRepository;

    @Mock
    private ProjectRepositoryPort projectRepository;

    @Mock
    private CurrentUserPort currentUserPort;

    @InjectMocks
    private ListGcesUseCaseImpl useCase;

    @Test
    void shouldListGraphsFromAllProjectsOfCurrentUser() {
        UUID userId = UUID.randomUUID();
        UUID projectIdOne = UUID.randomUUID();
        UUID projectIdTwo = UUID.randomUUID();

        when(currentUserPort.getCurrentUser())
                .thenReturn(new AuthenticatedUser(userId, "teste@teste.com", UserProfileEnum.USUARIO));
        when(projectRepository.findAllByUserId(userId)).thenReturn(List.of(
                new Project(projectIdOne, "Projeto 1", "Descricao 1", userId),
                new Project(projectIdTwo, "Projeto 2", "Descricao 2", userId)
        ));
        when(gceRepository.findAllByProjectId(projectIdOne)).thenReturn(List.of(buildGraph(projectIdOne, "GCE 1")));
        when(gceRepository.findAllByProjectId(projectIdTwo)).thenReturn(List.of(
                buildGraph(projectIdTwo, "GCE 2"),
                buildGraph(projectIdTwo, "GCE 3")
        ));

        List<GceOutput> output = useCase.execute();

        assertEquals(3, output.size());
        assertEquals("GCE 1", output.get(0).name());
        assertEquals("GCE 2", output.get(1).name());
        assertEquals("GCE 3", output.get(2).name());
        verify(currentUserPort).getCurrentUser();
        verify(projectRepository).findAllByUserId(userId);
        verify(gceRepository).findAllByProjectId(projectIdOne);
        verify(gceRepository).findAllByProjectId(projectIdTwo);
    }

    private Gce buildGraph(UUID projectId, String name) {
        return new Gce(
                UUID.randomUUID(),
                projectId,
                name,
                "Descricao",
                false,
                List.of(
                        GceNode.cause(UUID.randomUUID(), "C1", "Causa 1"),
                        GceNode.operator(UUID.randomUUID(), "O1", "Operador", GceOperatorTypeEnum.AND),
                        GceNode.effect(UUID.randomUUID(), "E1", "Efeito 1")
                ),
                List.of(
                        new GceEdge(UUID.randomUUID(), "C1", "O1", GceEdgeTypeEnum.IDENTITY),
                        new GceEdge(UUID.randomUUID(), "O1", "E1", GceEdgeTypeEnum.IDENTITY)
                ),
                List.of()
        );
    }
}
