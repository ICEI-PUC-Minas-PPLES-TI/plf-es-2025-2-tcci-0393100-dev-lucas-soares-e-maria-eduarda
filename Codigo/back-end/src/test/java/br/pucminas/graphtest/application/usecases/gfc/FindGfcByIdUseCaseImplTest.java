package br.pucminas.graphtest.application.usecases.gfc;

import br.pucminas.graphtest.application.domain.gfc.model.Gfc;
import br.pucminas.graphtest.application.domain.gfc.model.GfcNode;
import br.pucminas.graphtest.application.domain.project.model.Project;
import br.pucminas.graphtest.application.exception.GfcNotFoundException;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcOutput;
import br.pucminas.graphtest.application.port.output.repositories.GfcRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static br.pucminas.graphtest.application.domain.gfc.rules.GfcDomainRules.JAVA_LANGUAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindGfcByIdUseCaseImplTest {

    @Mock
    private GfcRepositoryPort gfcRepositoryPort;

    @Mock
    private ProjectAccessService projectAccessService;

    @InjectMocks
    private FindGfcByIdUseCaseImpl useCase;

    @Test
    void shouldFindGfcByIdAndAuthorizeProjectAccess() {
        UUID gfcId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID sourceFileId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        Gfc gfc = Gfc.persisted(
                gfcId,
                projectId,
                sourceFileId,
                "int soma(int a, int b)",
                "GFC soma",
                "Descricao",
                JAVA_LANGUAGE,
                List.of(
                        GfcNode.start(UUID.randomUUID(), "N0", "Inicio"),
                        GfcNode.statement(UUID.randomUUID(), "N1", "int x = 1", 1, 1),
                        GfcNode.statement(UUID.randomUUID(), "N2", "x++", 2, 2),
                        GfcNode.statement(UUID.randomUUID(), "N3", "x--", 3, 3),
                        GfcNode.statement(UUID.randomUUID(), "N5", "return x", 5, 5),
                        GfcNode.end(UUID.randomUUID(), "N_END", "Fim"),
                        GfcNode.statement(UUID.randomUUID(), "N4", "x = 0", 4, 4)
                ),
                List.of()
        );
        gfc.setCreatedAt(createdAt);
        when(gfcRepositoryPort.findById(gfcId)).thenReturn(Optional.of(gfc));
        when(projectAccessService.findAuthorizedProject(projectId))
                .thenReturn(new Project(projectId, "Projeto", "Descricao", UUID.randomUUID()));

        GfcOutput output = useCase.execute(gfcId);

        verify(projectAccessService).findAuthorizedProject(projectId);
        assertEquals(gfcId, output.id());
        assertEquals(projectId, output.projectId());
        assertEquals(sourceFileId, output.sourceFileId());
        assertEquals("int soma(int a, int b)", output.methodSignature());
        assertEquals(createdAt, output.createdAt());
        assertEquals(List.of("N0", "N1", "N2", "N3", "N4", "N5", "N_END"),
                output.nodes().stream().map(node -> node.code()).toList());
    }

    @Test
    void shouldThrowNotFoundWhenGfcDoesNotExist() {
        UUID gfcId = UUID.randomUUID();
        when(gfcRepositoryPort.findById(gfcId)).thenReturn(Optional.empty());

        assertThrows(GfcNotFoundException.class, () -> useCase.execute(gfcId));
        verifyNoInteractions(projectAccessService);
    }
}
