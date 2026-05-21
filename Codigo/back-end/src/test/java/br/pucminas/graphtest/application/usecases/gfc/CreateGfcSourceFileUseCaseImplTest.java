package br.pucminas.graphtest.application.usecases.gfc;

import br.pucminas.graphtest.application.domain.gfc.model.GfcSourceFile;
import br.pucminas.graphtest.application.domain.project.model.Project;
import br.pucminas.graphtest.application.port.input.gfc.records.CreateGfcSourceFileInput;
import br.pucminas.graphtest.application.port.input.gfc.records.CreateGfcSourceFileOutput;
import br.pucminas.graphtest.application.port.output.repositories.GfcSourceFileRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateGfcSourceFileUseCaseImplTest {

    @Mock
    private ProjectAccessService projectAccessService;

    @Mock
    private GfcSourceFileRepositoryPort gfcSourceFileRepositoryPort;

    @InjectMocks
    private CreateGfcSourceFileUseCaseImpl useCase;

    @Test
    void shouldAuthorizeProjectAndPersistJavaSourceFile() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID sourceFileId = UUID.randomUUID();
        CreateGfcSourceFileInput input = new CreateGfcSourceFileInput(
                projectId,
                "Exemplo.java",
                "class Exemplo {}"
        );
        when(projectAccessService.findAuthorizedProject(projectId))
                .thenReturn(new Project(projectId, "Projeto", "Descricao", userId));
        when(gfcSourceFileRepositoryPort.save(org.mockito.ArgumentMatchers.any(GfcSourceFile.class)))
                .thenAnswer(invocation -> {
                    GfcSourceFile sourceFile = invocation.getArgument(0);
                    sourceFile.setId(sourceFileId);
                    return sourceFile;
                });

        CreateGfcSourceFileOutput output = useCase.execute(input);

        ArgumentCaptor<GfcSourceFile> sourceFileCaptor = ArgumentCaptor.forClass(GfcSourceFile.class);
        verify(projectAccessService).findAuthorizedProject(projectId);
        verify(gfcSourceFileRepositoryPort).save(sourceFileCaptor.capture());
        assertEquals(projectId, sourceFileCaptor.getValue().getProjectId());
        assertEquals("Exemplo.java", sourceFileCaptor.getValue().getFileName());
        assertEquals("class Exemplo {}", sourceFileCaptor.getValue().getContent());
        assertEquals("Java", sourceFileCaptor.getValue().getLanguage());
        assertEquals(sourceFileId, output.sourceFileId());
    }
}
