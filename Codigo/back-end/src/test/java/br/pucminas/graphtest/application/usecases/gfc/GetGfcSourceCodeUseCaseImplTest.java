package br.pucminas.graphtest.application.usecases.gfc;

import br.pucminas.graphtest.application.domain.gfc.model.GfcSourceFile;
import br.pucminas.graphtest.application.domain.project.model.Project;
import br.pucminas.graphtest.application.exception.GfcSourceFileNotFoundException;
import br.pucminas.graphtest.application.exception.UnauthorizedUserException;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceCodeOutput;
import br.pucminas.graphtest.application.port.output.repositories.GfcSourceFileRepositoryPort;
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
class GetGfcSourceCodeUseCaseImplTest {

    @Mock
    private ProjectAccessService projectAccessService;

    @Mock
    private GfcSourceFileRepositoryPort gfcSourceFileRepositoryPort;

    @InjectMocks
    private GetGfcSourceCodeUseCaseImpl useCase;

    @Test
    void shouldReturnPersistedSourceCode() {
        UUID sourceFileId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String sourceCode = "class Exemplo {}";
        GfcSourceFile sourceFile = new GfcSourceFile(
                sourceFileId,
                projectId,
                "Exemplo.java",
                sourceCode,
                "Java"
        );
        when(gfcSourceFileRepositoryPort.findById(sourceFileId)).thenReturn(Optional.of(sourceFile));
        when(projectAccessService.findAuthorizedProject(projectId))
                .thenReturn(new Project(projectId, "Projeto", "Descricao", userId));

        GfcSourceCodeOutput output = useCase.execute(sourceFileId);

        verify(projectAccessService).findAuthorizedProject(projectId);
        assertEquals(sourceCode, output.sourceCode());
    }

    @Test
    void shouldThrowNotFoundWhenSourceFileDoesNotExist() {
        UUID sourceFileId = UUID.randomUUID();
        when(gfcSourceFileRepositoryPort.findById(sourceFileId)).thenReturn(Optional.empty());

        assertThrows(GfcSourceFileNotFoundException.class, () -> useCase.execute(sourceFileId));
        verifyNoInteractions(projectAccessService);
    }

    @Test
    void shouldPropagateAuthorizationFailureWhenUserCannotAccessSourceFileProject() {
        UUID sourceFileId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        GfcSourceFile sourceFile = new GfcSourceFile(
                sourceFileId,
                projectId,
                "Exemplo.java",
                "class Exemplo {}",
                "Java"
        );
        when(gfcSourceFileRepositoryPort.findById(sourceFileId)).thenReturn(Optional.of(sourceFile));
        when(projectAccessService.findAuthorizedProject(projectId))
                .thenThrow(new UnauthorizedUserException("Usuario nao possui permissao para acessar o projeto"));

        assertThrows(UnauthorizedUserException.class, () -> useCase.execute(sourceFileId));
        verify(projectAccessService).findAuthorizedProject(projectId);
    }
}
