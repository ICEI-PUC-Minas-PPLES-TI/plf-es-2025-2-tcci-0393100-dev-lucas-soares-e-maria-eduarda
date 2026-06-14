package br.pucminas.graphtest.adapters.inbound.util;

import br.pucminas.graphtest.adapters.inbound.dto.decisiontable.GenerateFunctionalTestSignatureResponseDTO;
import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableCellValueEnum;
import br.pucminas.graphtest.application.port.input.decisiontable.records.FunctionalTestActionOutput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.FunctionalTestConditionOutput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.FunctionalTestMethodSignatureOutput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.GenerateFunctionalTestSignatureOutput;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DecisionTableDtoConverterUtilTest {

    @Test
    void shouldConvertFunctionalTestSignatureOutputWithTraceability() {
        UUID tableId = UUID.randomUUID();
        UUID ruleId = UUID.randomUUID();
        UUID conditionId = UUID.randomUUID();
        UUID actionId = UUID.randomUUID();
        String code = "@Test\nvoid testeFuncional01() {\n\n}";
        GenerateFunctionalTestSignatureOutput output = new GenerateFunctionalTestSignatureOutput(
                tableId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Tabela",
                1,
                List.of(new FunctionalTestMethodSignatureOutput(
                        ruleId,
                        "R1",
                        "testeFuncional01",
                        List.of(new FunctionalTestConditionOutput(conditionId, "C1", "condicao", DecisionTableCellValueEnum.NO)),
                        List.of(new FunctionalTestActionOutput(actionId, "E1", "acao", DecisionTableCellValueEnum.YES)),
                        code
                )),
                code,
                List.of("Aviso")
        );

        GenerateFunctionalTestSignatureResponseDTO dto = DecisionTableDtoConverterUtil.toDto(output);

        assertEquals(tableId, dto.decisionTableId());
        assertEquals(1, dto.rulesCount());
        assertEquals(ruleId, dto.testMethods().getFirst().ruleId());
        assertEquals(conditionId, dto.testMethods().getFirst().conditions().getFirst().conditionId());
        assertEquals(actionId, dto.testMethods().getFirst().actions().getFirst().actionId());
        assertEquals(DecisionTableCellValueEnum.NO, dto.testMethods().getFirst().conditions().getFirst().value());
        assertEquals(DecisionTableCellValueEnum.YES, dto.testMethods().getFirst().actions().getFirst().value());
        assertEquals(List.of("Aviso"), dto.warnings());
        assertEquals(code, dto.generatedCode());
    }

    @Test
    void shouldSerializeUnifiedCellValueEnumWithSameJsonValue() throws JsonProcessingException {
        FunctionalTestConditionOutput output = new FunctionalTestConditionOutput(
                UUID.randomUUID(),
                "C1",
                "condicao",
                DecisionTableCellValueEnum.YES
        );

        String json = new ObjectMapper().writeValueAsString(output);

        assertTrue(json.contains("\"value\":\"YES\""));
    }
}
