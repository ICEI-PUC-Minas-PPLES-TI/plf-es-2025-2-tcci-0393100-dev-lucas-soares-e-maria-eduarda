package br.pucminas.graphtest.adapters.inbound.controller;

import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcSourceCodeDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcSourceMethodDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.PreviewGfcDTO;
import br.pucminas.graphtest.application.port.input.gfc.ListGfcSourceMethodsUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.PreviewGfcUseCasePort;
import br.pucminas.graphtest.application.exception.JavaSourceFileException;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceMethodOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.ListGfcSourceMethodsInput;
import br.pucminas.graphtest.application.port.input.gfc.records.PreviewGfcInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GfcControllerImplTest {

    @Mock
    private ListGfcSourceMethodsUseCasePort listGfcSourceMethodsUseCasePort;

    @Mock
    private PreviewGfcUseCasePort previewGfcUseCasePort;

    @InjectMocks
    private GfcControllerImpl controller;

    @Test
    void shouldPreviewGfcThroughInputPort() {
        UUID projectId = UUID.randomUUID();
        UUID graphId = UUID.randomUUID();
        PreviewGfcDTO request = new PreviewGfcDTO(projectId, "GFC", "Descricao", "int x = 1;", "void m()");
        GfcOutput output = new GfcOutput(
                graphId,
                projectId,
                "GFC",
                "Descricao",
                "int x = 1;",
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
        assertEquals(output.sourceCode(), response.getBody().sourceCode());
    }

    @Test
    void shouldReturnSourceCodeFromUploadedJavaFile() {
        String sourceCode = "class Exemplo { int soma(int a, int b) { return a + b; } }";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "Exemplo.java",
                "text/plain",
                sourceCode.getBytes()
        );

        ResponseEntity<GfcSourceCodeDTO> response = controller.source(file);

        assertEquals(sourceCode, response.getBody().sourceCode());
    }

    @Test
    void shouldListMethodsFromUploadedJavaFileThroughInputPort() {
        String sourceCode = "class Exemplo { int soma(int a, int b) { return a + b; } }";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "Exemplo.java",
                "text/plain",
                sourceCode.getBytes()
        );
        GfcSourceMethodOutput methodOutput = new GfcSourceMethodOutput("soma", "int soma(int a, int b)", 1, 1);
        when(listGfcSourceMethodsUseCasePort.execute(org.mockito.ArgumentMatchers.any(ListGfcSourceMethodsInput.class)))
                .thenReturn(List.of(methodOutput));

        ResponseEntity<List<GfcSourceMethodDTO>> response = controller.listMethods(file);

        ArgumentCaptor<ListGfcSourceMethodsInput> inputCaptor = ArgumentCaptor.forClass(ListGfcSourceMethodsInput.class);
        verify(listGfcSourceMethodsUseCasePort).execute(inputCaptor.capture());
        assertEquals(sourceCode, inputCaptor.getValue().sourceCode());
        assertEquals("soma", response.getBody().getFirst().name());
        assertEquals("int soma(int a, int b)", response.getBody().getFirst().signature());
    }

    @Test
    void shouldRejectNonJavaFileUpload() {
        MockMultipartFile file = new MockMultipartFile("file", "Exemplo.txt", "text/plain", "class Exemplo {}".getBytes());

        org.junit.jupiter.api.Assertions.assertThrows(JavaSourceFileException.class, () -> controller.source(file));
    }
}
