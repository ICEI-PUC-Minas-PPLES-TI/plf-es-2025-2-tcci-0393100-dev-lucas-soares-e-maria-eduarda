package br.pucminas.graphtest.application.usecases.gfc;

import br.pucminas.graphtest.application.domain.gfc.model.GfcSourceFile;
import br.pucminas.graphtest.application.domain.project.model.Project;
import br.pucminas.graphtest.application.exception.GfcSourceFileNotFoundException;
import br.pucminas.graphtest.application.port.output.repositories.GfcRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.GfcSourceFileRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteGfcSourceFileUseCaseImplTest {

    @Mock
    private GfcSourceFileRepositoryPort gfcSourceFileRepositoryPort;

    @Mock
    private GfcRepositoryPort gfcRepositoryPort;

    @Mock
    private ProjectAccessService projectAccessService;

    @InjectMocks
    private DeleteGfcSourceFileUseCaseImpl useCase;

    @Test
    void shouldDeleteSourceFileAndAssociatedGfcs() {
        UUID sourceFileId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        GfcSourceFile sourceFile = new GfcSourceFile(sourceFileId, projectId, "Exemplo.java", "class Exemplo {}", "Java");
        when(gfcSourceFileRepositoryPort.findById(sourceFileId)).thenReturn(Optional.of(sourceFile));
        when(projectAccessService.findAuthorizedProject(projectId))
                .thenReturn(new Project(projectId, "Projeto", "Descricao", userId));

        useCase.execute(sourceFileId);

        InOrder inOrder = inOrder(projectAccessService, gfcRepositoryPort, gfcSourceFileRepositoryPort);
        inOrder.verify(projectAccessService).findAuthorizedProject(projectId);
        inOrder.verify(gfcRepositoryPort).deleteAllBySourceFileId(sourceFileId);
        inOrder.verify(gfcSourceFileRepositoryPort).deleteById(sourceFileId);
    }

    @Test
    void shouldThrowNotFoundWhenSourceFileDoesNotExist() {
        UUID sourceFileId = UUID.randomUUID();
        when(gfcSourceFileRepositoryPort.findById(sourceFileId)).thenReturn(Optional.empty());

        assertThrows(GfcSourceFileNotFoundException.class, () -> useCase.execute(sourceFileId));
        verifyNoInteractions(projectAccessService, gfcRepositoryPort);
    }
}
