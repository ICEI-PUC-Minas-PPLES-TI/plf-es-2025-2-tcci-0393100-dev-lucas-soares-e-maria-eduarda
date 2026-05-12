package br.pucminas.graphtest.application.usecases.gfc;

import br.pucminas.graphtest.application.domain.gfc.model.GfcSourceFile;
import br.pucminas.graphtest.application.domain.project.model.Project;
import br.pucminas.graphtest.application.exception.GfcSourceFileNotFoundException;
import br.pucminas.graphtest.application.exception.UnauthorizedUserException;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceMethodOutput;
import br.pucminas.graphtest.application.port.output.repositories.GfcSourceFileRepositoryPort;
import br.pucminas.graphtest.application.service.gfc.interfaces.GfcSourceMethodListingService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListGfcSourceMethodsUseCaseImplTest {

    @Mock
    private ProjectAccessService projectAccessService;

    @Mock
    private GfcSourceFileRepositoryPort gfcSourceFileRepositoryPort;

    @Mock
    private GfcSourceMethodListingService gfcSourceMethodListingService;

    @InjectMocks
    private ListGfcSourceMethodsUseCaseImpl useCase;

    @Test
    void shouldListMethodsFromPersistedJavaSource() {
        UUID sourceFileId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String sourceCode = """
                        class Exemplo {
                            int soma(int a, int b) {
                                return a + b;
                            }

                            boolean valido(String email) {
                                return email != null;
                            }
                        }
                        """;
        GfcSourceFile sourceFile = new GfcSourceFile(sourceFileId, projectId, "Exemplo.java", sourceCode, "Java");
        GfcSourceMethodOutput firstMethod = new GfcSourceMethodOutput("soma", "int soma(int a, int b)", 2, 4);
        GfcSourceMethodOutput secondMethod = new GfcSourceMethodOutput("valido", "boolean valido(String email)", 6, 8);
        when(gfcSourceFileRepositoryPort.findById(sourceFileId)).thenReturn(Optional.of(sourceFile));
        when(projectAccessService.findAuthorizedProject(projectId))
                .thenReturn(new Project(projectId, "Projeto", "Descricao", userId));
        when(gfcSourceMethodListingService.listMethods(sourceCode)).thenReturn(List.of(firstMethod, secondMethod));

        List<GfcSourceMethodOutput> methods = useCase.execute(sourceFileId);

        verify(projectAccessService).findAuthorizedProject(projectId);
        verify(gfcSourceMethodListingService).listMethods(sourceCode);
        assertEquals(2, methods.size());
        assertEquals("soma", methods.get(0).name());
        assertEquals("int soma(int a, int b)", methods.get(0).signature());
        assertEquals(2, methods.get(0).startLine());
        assertEquals("boolean valido(String email)", methods.get(1).signature());
    }

    @Test
    void shouldThrowNotFoundWhenSourceFileDoesNotExist() {
        UUID sourceFileId = UUID.randomUUID();
        when(gfcSourceFileRepositoryPort.findById(sourceFileId)).thenReturn(Optional.empty());

        assertThrows(GfcSourceFileNotFoundException.class, () -> useCase.execute(sourceFileId));
        verifyNoInteractions(projectAccessService, gfcSourceMethodListingService);
    }

    @Test
    void shouldPropagateAuthorizationFailureWhenUserCannotAccessSourceFileProject() {
        UUID sourceFileId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        GfcSourceFile sourceFile = new GfcSourceFile(sourceFileId, projectId, "Exemplo.java", "class Exemplo {}", "Java");
        when(gfcSourceFileRepositoryPort.findById(sourceFileId)).thenReturn(Optional.of(sourceFile));
        when(projectAccessService.findAuthorizedProject(projectId))
                .thenThrow(new UnauthorizedUserException("Usuario nao possui permissao para acessar o projeto"));

        assertThrows(UnauthorizedUserException.class, () -> useCase.execute(sourceFileId));
        verify(projectAccessService).findAuthorizedProject(projectId);
        verifyNoInteractions(gfcSourceMethodListingService);
    }
}
