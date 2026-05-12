package br.pucminas.graphtest.application.usecases.gfc;

import br.pucminas.graphtest.application.domain.gfc.model.Gfc;
import br.pucminas.graphtest.application.domain.gfc.model.GfcNode;
import br.pucminas.graphtest.application.domain.project.model.Project;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.PreviewGfcInput;
import br.pucminas.graphtest.application.service.gfc.interfaces.GfcPreviewGenerationService;
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
class PreviewGfcUseCaseImplTest {

    @Mock
    private ProjectAccessService projectAccessService;

    @Mock
    private GfcPreviewGenerationService gfcPreviewGenerationService;

    @InjectMocks
    private PreviewGfcUseCaseImpl useCase;

    @Test
    void shouldAuthorizeProjectAndReturnPreviewWithoutPersistingGraph() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID graphId = UUID.randomUUID();
        PreviewGfcInput input = new PreviewGfcInput(projectId, "GFC", "Descricao", "class A { void m() {} }", "void m()");
        Gfc graph = Gfc.preview(
                graphId,
                projectId,
                input.methodSignature(),
                "GFC",
                "Descricao",
                "Java",
                List.of(GfcNode.start(UUID.randomUUID(), "N0", "Inicio")),
                List.of()
        );

        when(projectAccessService.findAuthorizedProject(projectId))
                .thenReturn(new Project(projectId, "Projeto", "Descricao", userId));
        when(gfcPreviewGenerationService.generate(input)).thenReturn(graph);

        GfcOutput output = useCase.execute(input);

        assertEquals(graphId, output.id());
        assertEquals(projectId, output.projectId());
        assertEquals(1, output.nodes().size());
        verify(projectAccessService).findAuthorizedProject(projectId);
        verify(gfcPreviewGenerationService).generate(input);
    }
}
