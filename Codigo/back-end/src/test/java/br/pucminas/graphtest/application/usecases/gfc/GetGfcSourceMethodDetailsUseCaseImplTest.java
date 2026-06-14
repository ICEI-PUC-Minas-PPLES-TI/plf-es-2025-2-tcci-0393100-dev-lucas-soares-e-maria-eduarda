package br.pucminas.graphtest.application.usecases.gfc;

import br.pucminas.graphtest.application.domain.gfc.model.GfcSourceFile;
import br.pucminas.graphtest.application.domain.project.model.Project;
import br.pucminas.graphtest.application.exception.GfcSourceFileNotFoundException;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceMethodDetailsOutput;
import br.pucminas.graphtest.application.port.output.repositories.GfcSourceFileRepositoryPort;
import br.pucminas.graphtest.application.service.gfc.interfaces.GfcSourceMethodDetailsService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetGfcSourceMethodDetailsUseCaseImplTest {

    @Mock
    private ProjectAccessService projectAccessService;

    @Mock
    private GfcSourceFileRepositoryPort gfcSourceFileRepositoryPort;

    @Mock
    private GfcSourceMethodDetailsService gfcSourceMethodDetailsService;

    @InjectMocks
    private GetGfcSourceMethodDetailsUseCaseImpl useCase;

    @Test
    void shouldReturnMethodDetailsFromPersistedSourceFile() {
        UUID sourceFileId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String sourceCode = "class Exemplo { int soma(int a, int b) { return a + b; } }";
        String signature = "int soma(int a, int b)";
        GfcSourceFile sourceFile = new GfcSourceFile(sourceFileId, projectId, "Exemplo.java", sourceCode, "Java");
        GfcSourceMethodDetailsOutput expected = new GfcSourceMethodDetailsOutput(
                "soma",
                signature,
                1,
                1,
                "int soma(int a, int b) {\n    return a + b;\n}"
        );
        when(gfcSourceFileRepositoryPort.findById(sourceFileId)).thenReturn(Optional.of(sourceFile));
        when(projectAccessService.findAuthorizedProject(projectId))
                .thenReturn(new Project(projectId, "Projeto", "Descricao", userId));
        when(gfcSourceMethodDetailsService.getDetails(sourceCode, signature)).thenReturn(expected);

        GfcSourceMethodDetailsOutput output = useCase.execute(projectId, sourceFileId, signature);

        verify(projectAccessService).findAuthorizedProject(projectId);
        verify(gfcSourceMethodDetailsService).getDetails(sourceCode, signature);
        assertEquals(expected, output);
    }

    @Test
    void shouldThrowNotFoundWhenSourceFileDoesNotExist() {
        UUID sourceFileId = UUID.randomUUID();
        when(gfcSourceFileRepositoryPort.findById(sourceFileId)).thenReturn(Optional.empty());

        assertThrows(GfcSourceFileNotFoundException.class, () -> useCase.execute(UUID.randomUUID(), sourceFileId, "void m()"));
        verifyNoInteractions(projectAccessService, gfcSourceMethodDetailsService);
    }
}
