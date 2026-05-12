package br.pucminas.graphtest.adapters.inbound.controller;

import br.pucminas.graphtest.adapters.inbound.dto.gfc.CreateGfcSourceFileResponseDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.DeleteGfcSourceFileResponseDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcSourceCodeDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcSourceFileDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcSourceMethodDTO;
import br.pucminas.graphtest.application.exception.JavaSourceFileException;
import br.pucminas.graphtest.application.port.input.gfc.CreateGfcSourceFileUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.DeleteGfcSourceFileUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.FindGfcSourceFileByIdUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.GetGfcSourceCodeUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.ListGfcSourceFilesByProjectUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.ListGfcSourceMethodsUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.records.CreateGfcSourceFileInput;
import br.pucminas.graphtest.application.port.input.gfc.records.CreateGfcSourceFileOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceCodeOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceFileOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceMethodOutput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
class GfcSourceFileControllerImplTest {

    @Mock
    private CreateGfcSourceFileUseCasePort createGfcSourceFileUseCasePort;

    @Mock
    private FindGfcSourceFileByIdUseCasePort findGfcSourceFileByIdUseCasePort;

    @Mock
    private ListGfcSourceFilesByProjectUseCasePort listGfcSourceFilesByProjectUseCasePort;

    @Mock
    private GetGfcSourceCodeUseCasePort getGfcSourceCodeUseCasePort;

    @Mock
    private ListGfcSourceMethodsUseCasePort listGfcSourceMethodsUseCasePort;

    @Mock
    private DeleteGfcSourceFileUseCasePort deleteGfcSourceFileUseCasePort;

    @InjectMocks
    private GfcSourceFileControllerImpl controller;

    @Test
    void shouldCreateSourceFileThroughInputPort() {
        UUID projectId = UUID.randomUUID();
        UUID sourceFileId = UUID.randomUUID();
        String sourceCode = "class Exemplo { int soma(int a, int b) { return a + b; } }";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "Exemplo.java",
                "text/plain",
                sourceCode.getBytes()
        );
        when(createGfcSourceFileUseCasePort.execute(org.mockito.ArgumentMatchers.any(CreateGfcSourceFileInput.class)))
                .thenReturn(new CreateGfcSourceFileOutput(sourceFileId));

        ResponseEntity<CreateGfcSourceFileResponseDTO> response = controller.createSourceFile(projectId, file);

        ArgumentCaptor<CreateGfcSourceFileInput> inputCaptor = ArgumentCaptor.forClass(CreateGfcSourceFileInput.class);
        verify(createGfcSourceFileUseCasePort).execute(inputCaptor.capture());
        assertEquals(projectId, inputCaptor.getValue().projectId());
        assertEquals("Exemplo.java", inputCaptor.getValue().fileName());
        assertEquals(sourceCode, inputCaptor.getValue().content());
        assertEquals(CREATED, response.getStatusCode());
        assertEquals(sourceFileId, response.getBody().id_arquivo());
        assertEquals("arquivo cadastrado com sucesso", response.getBody().mensagem());
        assertEquals(CREATED.value(), response.getBody().status());
    }

    @Test
    void shouldFindSourceFileByIdThroughInputPort() {
        UUID sourceFileId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = createdAt.plusMinutes(1);
        GfcSourceFileOutput output = new GfcSourceFileOutput(sourceFileId, projectId, "Exemplo.java", "Java", createdAt, updatedAt);
        when(findGfcSourceFileByIdUseCasePort.execute(sourceFileId)).thenReturn(output);

        ResponseEntity<GfcSourceFileDTO> response = controller.findById(sourceFileId);

        verify(findGfcSourceFileByIdUseCasePort).execute(sourceFileId);
        assertEquals(sourceFileId, response.getBody().id());
        assertEquals(projectId, response.getBody().projectId());
        assertEquals("Exemplo.java", response.getBody().fileName());
        assertEquals("Java", response.getBody().language());
        assertEquals(createdAt, response.getBody().createdAt());
        assertEquals(updatedAt, response.getBody().updatedAt());
    }

    @Test
    void shouldListSourceFilesByProjectThroughInputPort() {
        UUID sourceFileId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        GfcSourceFileOutput output = new GfcSourceFileOutput(
                sourceFileId,
                projectId,
                "Exemplo.java",
                "Java",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(listGfcSourceFilesByProjectUseCasePort.execute(projectId)).thenReturn(List.of(output));

        ResponseEntity<List<GfcSourceFileDTO>> response = controller.listByProject(projectId);

        verify(listGfcSourceFilesByProjectUseCasePort).execute(projectId);
        assertEquals(1, response.getBody().size());
        assertEquals(sourceFileId, response.getBody().getFirst().id());
        assertEquals("Exemplo.java", response.getBody().getFirst().fileName());
    }

    @Test
    void shouldReturnPersistedSourceCodeThroughInputPort() {
        UUID sourceFileId = UUID.randomUUID();
        String sourceCode = "class Exemplo { int soma(int a, int b) { return a + b; } }";
        when(getGfcSourceCodeUseCasePort.execute(sourceFileId))
                .thenReturn(new GfcSourceCodeOutput(sourceCode));

        ResponseEntity<GfcSourceCodeDTO> response = controller.getSourceCode(sourceFileId);

        verify(getGfcSourceCodeUseCasePort).execute(sourceFileId);
        assertEquals(sourceCode, response.getBody().sourceCode());
    }

    @Test
    void shouldListMethodsFromPersistedSourceFileThroughInputPort() {
        UUID sourceFileId = UUID.randomUUID();
        GfcSourceMethodOutput methodOutput = new GfcSourceMethodOutput("soma", "int soma(int a, int b)", 1, 1);
        when(listGfcSourceMethodsUseCasePort.execute(sourceFileId)).thenReturn(List.of(methodOutput));

        ResponseEntity<List<GfcSourceMethodDTO>> response = controller.listMethods(sourceFileId);

        verify(listGfcSourceMethodsUseCasePort).execute(sourceFileId);
        assertEquals("soma", response.getBody().getFirst().name());
        assertEquals("int soma(int a, int b)", response.getBody().getFirst().signature());
    }

    @Test
    void shouldDeleteSourceFileThroughInputPort() {
        UUID sourceFileId = UUID.randomUUID();
        doNothing().when(deleteGfcSourceFileUseCasePort).execute(sourceFileId);

        ResponseEntity<DeleteGfcSourceFileResponseDTO> response = controller.delete(sourceFileId);

        verify(deleteGfcSourceFileUseCasePort).execute(sourceFileId);
        assertEquals(OK, response.getStatusCode());
        assertEquals("Arquivo-fonte removido com sucesso", response.getBody().mensagem());
        assertEquals(OK.value(), response.getBody().status());
    }

    @Test
    void shouldRejectNonJavaFileUpload() {
        MockMultipartFile file = new MockMultipartFile("file", "Exemplo.txt", "text/plain", "class Exemplo {}".getBytes());

        assertThrows(JavaSourceFileException.class, () -> controller.createSourceFile(UUID.randomUUID(), file));
    }
}
