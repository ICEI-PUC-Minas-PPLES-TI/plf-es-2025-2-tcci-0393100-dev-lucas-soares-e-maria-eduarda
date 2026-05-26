package br.pucminas.graphtest.adapters.inbound.controller;

import br.pucminas.graphtest.adapters.inbound.dto.decisiontable.GenerateFunctionalTestSignatureResponseDTO;
import br.pucminas.graphtest.application.port.input.decisiontable.DeleteDecisionTableByIdUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.FindDecisionTableByGceIdUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.FindDecisionTableByIdUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.FindDecisionTableStatusByIdUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.GenerateDecisionTableUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.GenerateFunctionalTestSignatureUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.ListDecisionTablesByProjectUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.ListDecisionTablesUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.PatchDecisionTableDetailsUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.PreviewDecisionTableUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.RefreshDecisionTableUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.records.FunctionalTestMethodSignatureOutput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.GenerateFunctionalTestSignatureOutput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
class DecisionTableControllerImplTest {

    @Mock
    private ListDecisionTablesUseCasePort listDecisionTablesUseCasePort;
    @Mock
    private GenerateDecisionTableUseCasePort generateDecisionTableUseCasePort;
    @Mock
    private FindDecisionTableByIdUseCasePort findDecisionTableByIdUseCasePort;
    @Mock
    private FindDecisionTableStatusByIdUseCasePort findDecisionTableStatusByIdUseCasePort;
    @Mock
    private FindDecisionTableByGceIdUseCasePort findDecisionTableByGceIdUseCasePort;
    @Mock
    private ListDecisionTablesByProjectUseCasePort listDecisionTablesByProjectUseCasePort;
    @Mock
    private PatchDecisionTableDetailsUseCasePort patchDecisionTableDetailsUseCasePort;
    @Mock
    private PreviewDecisionTableUseCasePort previewDecisionTableUseCasePort;
    @Mock
    private RefreshDecisionTableUseCasePort refreshDecisionTableUseCasePort;
    @Mock
    private DeleteDecisionTableByIdUseCasePort deleteDecisionTableByIdUseCasePort;
    @Mock
    private GenerateFunctionalTestSignatureUseCasePort generateFunctionalTestSignatureUseCasePort;

    @InjectMocks
    private DecisionTableControllerImpl controller;

    @Test
    void shouldGenerateFunctionalTestSignatureThroughInputPort() {
        UUID tableId = UUID.randomUUID();
        String code = "@Test\nvoid testeFuncional01() {\n\n}";
        GenerateFunctionalTestSignatureOutput output = new GenerateFunctionalTestSignatureOutput(
                tableId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Tabela Login",
                1,
                List.of(new FunctionalTestMethodSignatureOutput(UUID.randomUUID(), "R1", "testeFuncional01", List.of(), List.of(), code)),
                code,
                List.of()
        );
        when(generateFunctionalTestSignatureUseCasePort.execute(tableId)).thenReturn(output);

        ResponseEntity<GenerateFunctionalTestSignatureResponseDTO> response = controller.generateFunctionalTestSignature(tableId);

        verify(generateFunctionalTestSignatureUseCasePort).execute(tableId);
        assertEquals(OK, response.getStatusCode());
        assertEquals(tableId, response.getBody().decisionTableId());
        assertEquals(1, response.getBody().rulesCount());
        assertEquals("testeFuncional01", response.getBody().testMethods().getFirst().methodName());
        assertEquals(code, response.getBody().generatedCode());
    }
}
