package br.pucminas.graphtest.application.usecases.gce;

import br.pucminas.graphtest.application.domain.Gce;
import br.pucminas.graphtest.application.domain.Project;
import br.pucminas.graphtest.application.domain.enums.GceEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.enums.GceNodeTypeEnum;
import br.pucminas.graphtest.application.port.input.gce.records.CreateGceInput;
import br.pucminas.graphtest.application.port.input.gce.records.GceEdgeInput;
import br.pucminas.graphtest.application.port.input.gce.records.GceNodeInput;
import br.pucminas.graphtest.application.port.input.gce.records.GceOutput;
import br.pucminas.graphtest.application.port.input.gce.records.ValidationGceOutput;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.service.interfaces.GceValidationResultService;
import br.pucminas.graphtest.application.service.interfaces.ProjectAccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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

    @InjectMocks
    private CreateGceUseCaseImpl useCase;

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
                        new GceNodeInput("E1", "Efeito", GceNodeTypeEnum.EFFECT, null)
                ),
                List.of(new GceEdgeInput("C1", "E1", GceEdgeTypeEnum.IDENTITY)),
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
        verify(projectAccessService).findAuthorizedProject(projectId);
        verify(gceRepository).save(any(Gce.class));
    }
}
