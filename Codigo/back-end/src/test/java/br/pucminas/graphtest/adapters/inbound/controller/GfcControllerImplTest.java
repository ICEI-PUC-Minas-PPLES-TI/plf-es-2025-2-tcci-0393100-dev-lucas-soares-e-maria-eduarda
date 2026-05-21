package br.pucminas.graphtest.adapters.inbound.controller;

import br.pucminas.graphtest.adapters.inbound.dto.gfc.CreateGfcDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.CreateGfcResponseDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.CyclomaticComplexityResponseDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.DeleteGfcResponseDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcSummaryDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.PreviewGfcDTO;
import br.pucminas.graphtest.application.port.input.gfc.CreateGfcUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.CalculateCyclomaticComplexityUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.DeleteGfcUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.FindGfcByIdUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.ListGfcByProjectUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.PreviewGfcUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.records.CreateGfcInput;
import br.pucminas.graphtest.application.port.input.gfc.records.CreateGfcOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.CyclomaticComplexityOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcSummaryOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.PreviewGfcInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
class GfcControllerImplTest {

    @Mock
    private CreateGfcUseCasePort createGfcUseCasePort;

    @Mock
    private FindGfcByIdUseCasePort findGfcByIdUseCasePort;

    @Mock
    private DeleteGfcUseCasePort deleteGfcUseCasePort;

    @Mock
    private ListGfcByProjectUseCasePort listGfcByProjectUseCasePort;

    @Mock
    private PreviewGfcUseCasePort previewGfcUseCasePort;

    @Mock
    private CalculateCyclomaticComplexityUseCasePort calculateCyclomaticComplexityUseCasePort;

    @InjectMocks
    private GfcControllerImpl controller;

    @Test
    void shouldCreateGfcThroughInputPort() {
        UUID projectId = UUID.randomUUID();
        UUID sourceFileId = UUID.randomUUID();
        UUID gfcId = UUID.randomUUID();
        CreateGfcDTO request = new CreateGfcDTO(
                projectId,
                sourceFileId,
                "int soma(int a, int b)",
                "GFC soma",
                "Descricao"
        );
        when(createGfcUseCasePort.execute(org.mockito.ArgumentMatchers.any(CreateGfcInput.class)))
                .thenReturn(new CreateGfcOutput(gfcId));

        ResponseEntity<CreateGfcResponseDTO> response = controller.create(request);

        ArgumentCaptor<CreateGfcInput> inputCaptor = ArgumentCaptor.forClass(CreateGfcInput.class);
        verify(createGfcUseCasePort).execute(inputCaptor.capture());
        assertEquals(projectId, inputCaptor.getValue().projectId());
        assertEquals(sourceFileId, inputCaptor.getValue().sourceFileId());
        assertEquals("int soma(int a, int b)", inputCaptor.getValue().methodSignature());
        assertEquals(CREATED, response.getStatusCode());
        assertEquals(gfcId, response.getBody().id_gfc());
        assertEquals("Grafo de Fluxo de Controle criado com sucesso", response.getBody().mensagem());
        assertEquals(CREATED.value(), response.getBody().status());
    }

    @Test
    void shouldFindGfcByIdThroughInputPort() {
        UUID gfcId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID sourceFileId = UUID.randomUUID();
        GfcOutput output = new GfcOutput(
                gfcId,
                projectId,
                sourceFileId,
                "int soma(int a, int b)",
                "GFC soma",
                "Descricao",
                "Java",
                List.of(),
                List.of()
        );
        when(findGfcByIdUseCasePort.execute(gfcId)).thenReturn(output);

        ResponseEntity<GfcDTO> response = controller.findById(gfcId);

        verify(findGfcByIdUseCasePort).execute(gfcId);
        assertEquals(gfcId, response.getBody().id());
        assertEquals(projectId, response.getBody().projectId());
        assertEquals(sourceFileId, response.getBody().sourceFileId());
        assertEquals("int soma(int a, int b)", response.getBody().methodSignature());
        assertEquals("GFC soma", response.getBody().name());
    }

    @Test
    void shouldDeleteGfcThroughInputPort() {
        UUID gfcId = UUID.randomUUID();
        doNothing().when(deleteGfcUseCasePort).execute(gfcId);

        ResponseEntity<DeleteGfcResponseDTO> response = controller.delete(gfcId);

        verify(deleteGfcUseCasePort).execute(gfcId);
        assertEquals(OK, response.getStatusCode());
        assertEquals("Grafo de Fluxo de Controle removido com sucesso", response.getBody().mensagem());
        assertEquals(OK.value(), response.getBody().status());
    }

    @Test
    void shouldListGfcsByProjectThroughInputPort() {
        UUID projectId = UUID.randomUUID();
        UUID gfcId = UUID.randomUUID();
        UUID sourceFileId = UUID.randomUUID();
        GfcSummaryOutput output = new GfcSummaryOutput(
                gfcId,
                projectId,
                sourceFileId,
                "int soma(int a, int b)",
                "GFC soma",
                "Descricao",
                "Java"
        );
        when(listGfcByProjectUseCasePort.execute(projectId)).thenReturn(List.of(output));

        ResponseEntity<List<GfcSummaryDTO>> response = controller.listByProject(projectId);

        verify(listGfcByProjectUseCasePort).execute(projectId);
        assertEquals(1, response.getBody().size());
        assertEquals(gfcId, response.getBody().getFirst().id());
        assertEquals(sourceFileId, response.getBody().getFirst().sourceFileId());
        assertEquals("GFC soma", response.getBody().getFirst().name());
    }

    @Test
    void shouldPreviewGfcThroughInputPort() {
        UUID projectId = UUID.randomUUID();
        UUID graphId = UUID.randomUUID();
        PreviewGfcDTO request = new PreviewGfcDTO(projectId, "GFC", "Descricao", "int x = 1;", "void m()");
        GfcOutput output = new GfcOutput(
                graphId,
                projectId,
                null,
                "void m()",
                "GFC",
                "Descricao",
                "Java",
                List.of(),
                List.of()
        );
        when(previewGfcUseCasePort.execute(org.mockito.ArgumentMatchers.any(PreviewGfcInput.class)))
                .thenReturn(output);

        ResponseEntity<GfcDTO> response = controller.preview(request);

        ArgumentCaptor<PreviewGfcInput> inputCaptor = ArgumentCaptor.forClass(PreviewGfcInput.class);
        verify(previewGfcUseCasePort).execute(inputCaptor.capture());
        assertEquals(projectId, inputCaptor.getValue().projectId());
        assertEquals("GFC", inputCaptor.getValue().name());
        assertEquals("Descricao", inputCaptor.getValue().description());
        assertEquals("int x = 1;", inputCaptor.getValue().sourceCode());
        assertEquals("void m()", inputCaptor.getValue().methodSignature());
        assertEquals(output.id(), response.getBody().id());
        assertEquals(output.projectId(), response.getBody().projectId());
        assertEquals(output.name(), response.getBody().name());
        assertEquals(output.methodSignature(), response.getBody().methodSignature());
    }

    @Test
    void shouldCalculateCyclomaticComplexityThroughInputPort() {
        UUID gfcId = UUID.randomUUID();
        CyclomaticComplexityOutput output = new CyclomaticComplexityOutput(
                gfcId,
                10,
                15,
                6,
                7,
                7,
                "V(G) = e - n + 2",
                "V(G) = P + 1",
                List.of("Aviso")
        );
        when(calculateCyclomaticComplexityUseCasePort.execute(gfcId)).thenReturn(output);

        ResponseEntity<CyclomaticComplexityResponseDTO> response = controller.calculateCyclomaticComplexity(gfcId);

        verify(calculateCyclomaticComplexityUseCasePort).execute(gfcId);
        assertEquals(OK, response.getStatusCode());
        assertEquals(gfcId, response.getBody().gfcId());
        assertEquals(10, response.getBody().nodesCount());
        assertEquals(15, response.getBody().edgesCount());
        assertEquals(6, response.getBody().predicateNodesCount());
        assertEquals(7, response.getBody().cyclomaticComplexityByEdgesAndNodes());
        assertEquals(7, response.getBody().cyclomaticComplexityByPredicateNodes());
        assertEquals("V(G) = e - n + 2", response.getBody().formulaByEdgesAndNodes());
        assertEquals("V(G) = P + 1", response.getBody().formulaByPredicateNodes());
        assertEquals(List.of("Aviso"), response.getBody().warnings());
    }
}
