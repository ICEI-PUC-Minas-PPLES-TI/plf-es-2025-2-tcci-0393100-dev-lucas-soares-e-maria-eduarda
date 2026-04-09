package br.pucminas.graphtest.application.usecases.gce;

import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.domain.project.model.Project;
import br.pucminas.graphtest.application.domain.gce.enums.GceNodeTypeEnum;
import br.pucminas.graphtest.application.port.input.gce.records.GceNodeInput;
import br.pucminas.graphtest.application.port.input.gce.records.ValidateGceInput;
import br.pucminas.graphtest.application.port.input.gce.records.ValidationGceOutput;
import br.pucminas.graphtest.application.service.gce.interfaces.GceMutationService;
import br.pucminas.graphtest.application.service.gce.interfaces.GceValidationResultService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;
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
class ValidateGceUseCaseImplTest {

    @Mock
    private ProjectAccessService projectAccessService;

    @Mock
    private GceValidationResultService gceValidationResultService;

    @Mock
    private GceMutationService gceMutationService;

    @InjectMocks
    private ValidateGceUseCaseImpl useCase;

    @Test
    void shouldValidateGraphFromInputWithoutLoadingPersistedGraph() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        ValidateGceInput input = new ValidateGceInput(
                projectId,
                "GCE",
                "Descricao",
                false,
                List.of(new GceNodeInput("C1", "Causa", GceNodeTypeEnum.CAUSE, null, List.of(), List.of())),
                List.of(),
                List.of()
        );
        Gce graph = new Gce(
                null,
                projectId,
                "GCE",
                "Descricao",
                false,
                List.of(),
                List.of(),
                List.of()
        );

        when(projectAccessService.findAuthorizedProject(projectId))
                .thenReturn(new Project(projectId, "Projeto", "Descricao", userId));
        when(gceMutationService.toNodes(input.nodes())).thenReturn(List.of());
        when(gceMutationService.toEdges(input.nodes(), input.edges())).thenReturn(List.of());
        when(gceMutationService.toRestrictions(input.restrictions())).thenReturn(List.of());
        when(gceValidationResultService.validate(any(Gce.class)))
                .thenReturn(new ValidationGceOutput(List.of(), List.of()));

        ValidationGceOutput output = useCase.execute(input);

        verify(projectAccessService).findAuthorizedProject(projectId);
        verify(gceMutationService).toNodes(input.nodes());
        verify(gceMutationService).toEdges(input.nodes(), input.edges());
        verify(gceMutationService).toRestrictions(input.restrictions());
        verify(gceValidationResultService).validate(any(Gce.class));
        assertEquals(true, output.valid());
    }
}
