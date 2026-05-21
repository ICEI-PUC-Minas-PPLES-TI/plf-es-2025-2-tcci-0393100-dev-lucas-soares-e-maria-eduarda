package br.pucminas.graphtest.application.usecases.gfc;

import br.pucminas.graphtest.application.domain.gfc.model.Gfc;
import br.pucminas.graphtest.application.domain.project.model.Project;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcSummaryOutput;
import br.pucminas.graphtest.application.port.output.repositories.GfcRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static br.pucminas.graphtest.application.domain.gfc.rules.GfcDomainRules.JAVA_LANGUAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListGfcByProjectUseCaseImplTest {

    @Mock
    private GfcRepositoryPort gfcRepositoryPort;

    @Mock
    private ProjectAccessService projectAccessService;

    @InjectMocks
    private ListGfcByProjectUseCaseImpl useCase;

    @Test
    void shouldAuthorizeProjectAndReturnGfcSummaries() {
        UUID projectId = UUID.randomUUID();
        UUID firstGfcId = UUID.randomUUID();
        UUID secondGfcId = UUID.randomUUID();
        Gfc firstGfc = Gfc.persisted(
                firstGfcId,
                projectId,
                UUID.randomUUID(),
                "int soma(int a, int b)",
                "GFC soma",
                "Descricao soma",
                JAVA_LANGUAGE,
                List.of(),
                List.of()
        );
        Gfc secondGfc = Gfc.persisted(
                secondGfcId,
                projectId,
                UUID.randomUUID(),
                "void executar()",
                "GFC executar",
                "Descricao executar",
                JAVA_LANGUAGE,
                List.of(),
                List.of()
        );
        when(projectAccessService.findAuthorizedProject(projectId))
                .thenReturn(new Project(projectId, "Projeto", "Descricao", UUID.randomUUID()));
        when(gfcRepositoryPort.findAllByProjectId(projectId)).thenReturn(List.of(firstGfc, secondGfc));

        List<GfcSummaryOutput> output = useCase.execute(projectId);

        verify(projectAccessService).findAuthorizedProject(projectId);
        verify(gfcRepositoryPort).findAllByProjectId(projectId);
        assertEquals(2, output.size());
        assertEquals(firstGfcId, output.get(0).id());
        assertEquals("GFC soma", output.get(0).name());
        assertEquals(secondGfcId, output.get(1).id());
        assertEquals("void executar()", output.get(1).methodSignature());
    }

    @Test
    void shouldReturnEmptyListWhenProjectHasNoGfcs() {
        UUID projectId = UUID.randomUUID();
        when(projectAccessService.findAuthorizedProject(projectId))
                .thenReturn(new Project(projectId, "Projeto", "Descricao", UUID.randomUUID()));
        when(gfcRepositoryPort.findAllByProjectId(projectId)).thenReturn(List.of());

        List<GfcSummaryOutput> output = useCase.execute(projectId);

        verify(projectAccessService).findAuthorizedProject(projectId);
        verify(gfcRepositoryPort).findAllByProjectId(projectId);
        assertEquals(0, output.size());
    }
}
