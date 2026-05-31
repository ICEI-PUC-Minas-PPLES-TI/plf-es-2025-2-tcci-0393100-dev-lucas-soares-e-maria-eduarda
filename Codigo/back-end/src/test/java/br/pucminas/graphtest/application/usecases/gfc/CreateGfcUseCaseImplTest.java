package br.pucminas.graphtest.application.usecases.gfc;

import br.pucminas.graphtest.application.domain.gfc.model.Gfc;
import br.pucminas.graphtest.application.domain.gfc.model.GfcNode;
import br.pucminas.graphtest.application.domain.gfc.model.GfcSourceFile;
import br.pucminas.graphtest.application.domain.project.model.Project;
import br.pucminas.graphtest.application.exception.GfcSourceFileNotFoundException;
import br.pucminas.graphtest.application.exception.GfcSourceFileProjectMismatchException;
import br.pucminas.graphtest.application.port.input.gfc.records.CreateGfcInput;
import br.pucminas.graphtest.application.port.input.gfc.records.CreateGfcOutput;
import br.pucminas.graphtest.application.port.output.repositories.GfcRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.GfcSourceFileRepositoryPort;
import br.pucminas.graphtest.application.service.gfc.interfaces.GfcGenerationService;
import br.pucminas.graphtest.application.service.gfc.records.GfcGenerationInput;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static br.pucminas.graphtest.application.domain.gfc.rules.GfcDomainRules.JAVA_LANGUAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateGfcUseCaseImplTest {

    @Mock
    private ProjectAccessService projectAccessService;

    @Mock
    private GfcSourceFileRepositoryPort gfcSourceFileRepositoryPort;

    @Mock
    private GfcGenerationService gfcGenerationService;

    @Mock
    private GfcRepositoryPort gfcRepositoryPort;

    @InjectMocks
    private CreateGfcUseCaseImpl useCase;

    @Test
    void shouldAuthorizeProjectGenerateAndPersistGfcFromSourceFile() {
        UUID projectId = UUID.randomUUID();
        UUID sourceFileId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID gfcId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        String sourceCode = "class Exemplo { int soma(int a, int b) { return a + b; } }";
        CreateGfcInput input = new CreateGfcInput(
                projectId,
                sourceFileId,
                "int soma(int a, int b)",
                "GFC soma",
                "Descricao"
        );
        GfcSourceFile sourceFile = new GfcSourceFile(sourceFileId, projectId, "Exemplo.java", sourceCode, JAVA_LANGUAGE);
        Gfc generatedGfc = Gfc.reconstitute(
                gfcId,
                projectId,
                sourceFileId,
                input.methodSignature(),
                input.name(),
                input.description(),
                JAVA_LANGUAGE,
                List.of(GfcNode.start(UUID.randomUUID(), "N0", "Inicio")),
                List.of(),
                createdAt,
                null
        );
        when(projectAccessService.findAuthorizedProject(projectId))
                .thenReturn(new Project(projectId, "Projeto", "Descricao", userId));
        when(gfcSourceFileRepositoryPort.findById(sourceFileId)).thenReturn(Optional.of(sourceFile));
        when(gfcGenerationService.generate(org.mockito.ArgumentMatchers.any(GfcGenerationInput.class))).thenReturn(generatedGfc);
        when(gfcRepositoryPort.save(generatedGfc)).thenReturn(generatedGfc);

        CreateGfcOutput output = useCase.execute(input);

        ArgumentCaptor<GfcGenerationInput> generationInputCaptor = ArgumentCaptor.forClass(GfcGenerationInput.class);
        verify(projectAccessService).findAuthorizedProject(projectId);
        verify(gfcGenerationService).generate(generationInputCaptor.capture());
        verify(gfcRepositoryPort).save(generatedGfc);
        assertEquals(sourceCode, generationInputCaptor.getValue().sourceCode());
        assertEquals(sourceFileId, generationInputCaptor.getValue().sourceFileId());
        assertEquals(input.methodSignature(), generationInputCaptor.getValue().methodSignature());
        assertEquals(gfcId, output.gfcId());
        assertEquals(createdAt, output.createdAt());
        assertNotNull(output.createdAt());
    }

    @Test
    void shouldThrowNotFoundWhenSourceFileDoesNotExist() {
        UUID projectId = UUID.randomUUID();
        UUID sourceFileId = UUID.randomUUID();
        CreateGfcInput input = new CreateGfcInput(projectId, sourceFileId, "void m()", "GFC", null);
        when(projectAccessService.findAuthorizedProject(projectId))
                .thenReturn(new Project(projectId, "Projeto", "Descricao", UUID.randomUUID()));
        when(gfcSourceFileRepositoryPort.findById(sourceFileId)).thenReturn(Optional.empty());

        assertThrows(GfcSourceFileNotFoundException.class, () -> useCase.execute(input));
        verifyNoInteractions(gfcGenerationService, gfcRepositoryPort);
    }

    @Test
    void shouldRejectSourceFileFromAnotherProject() {
        UUID projectId = UUID.randomUUID();
        UUID sourceFileId = UUID.randomUUID();
        UUID otherProjectId = UUID.randomUUID();
        CreateGfcInput input = new CreateGfcInput(projectId, sourceFileId, "void m()", "GFC", null);
        GfcSourceFile sourceFile = new GfcSourceFile(sourceFileId, otherProjectId, "Exemplo.java", "class Exemplo {}", JAVA_LANGUAGE);
        when(projectAccessService.findAuthorizedProject(projectId))
                .thenReturn(new Project(projectId, "Projeto", "Descricao", UUID.randomUUID()));
        when(gfcSourceFileRepositoryPort.findById(sourceFileId)).thenReturn(Optional.of(sourceFile));

        assertThrows(GfcSourceFileProjectMismatchException.class, () -> useCase.execute(input));
        verifyNoInteractions(gfcGenerationService, gfcRepositoryPort);
    }
}
