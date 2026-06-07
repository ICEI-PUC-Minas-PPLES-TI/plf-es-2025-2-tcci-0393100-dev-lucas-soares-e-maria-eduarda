package br.pucminas.graphtest.application.usecases.gfc;

import br.pucminas.graphtest.application.domain.gfc.model.Gfc;
import br.pucminas.graphtest.application.domain.project.model.Project;
import br.pucminas.graphtest.application.exception.GfcNotFoundException;
import br.pucminas.graphtest.application.port.output.repositories.GfcRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static br.pucminas.graphtest.application.domain.gfc.rules.GfcDomainRules.JAVA_LANGUAGE;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteGfcUseCaseImplTest {

    @Mock
    private GfcRepositoryPort gfcRepositoryPort;

    @Mock
    private ProjectAccessService projectAccessService;

    @InjectMocks
    private DeleteGfcUseCaseImpl useCase;

    @Test
    void shouldAuthorizeAndDeleteGfcById() {
        UUID gfcId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        Gfc gfc = Gfc.persisted(
                gfcId,
                projectId,
                UUID.randomUUID(),
                "void executar()",
                "GFC executar",
                "Descricao",
                JAVA_LANGUAGE,
                List.of(),
                List.of()
        );
        when(gfcRepositoryPort.findById(gfcId)).thenReturn(Optional.of(gfc));
        when(projectAccessService.findAuthorizedProject(projectId))
                .thenReturn(new Project(projectId, "Projeto", "Descricao", UUID.randomUUID()));

        useCase.execute(projectId, gfcId);

        InOrder inOrder = inOrder(gfcRepositoryPort, projectAccessService);
        inOrder.verify(gfcRepositoryPort).findById(gfcId);
        inOrder.verify(projectAccessService).findAuthorizedProject(projectId);
        inOrder.verify(gfcRepositoryPort).deleteById(gfcId);
    }

    @Test
    void shouldThrowNotFoundWhenGfcDoesNotExist() {
        UUID gfcId = UUID.randomUUID();
        when(gfcRepositoryPort.findById(gfcId)).thenReturn(Optional.empty());

        assertThrows(GfcNotFoundException.class, () -> useCase.execute(UUID.randomUUID(), gfcId));
        verifyNoInteractions(projectAccessService);
    }
}
