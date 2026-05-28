package br.pucminas.graphtest.adapters.inbound.util;

import br.pucminas.graphtest.adapters.inbound.dto.decisiontable.GenerateFunctionalTestSignatureResponseDTO;
import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableActionValueEnum;
import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableConditionValueEnum;
import br.pucminas.graphtest.application.port.input.decisiontable.records.FunctionalTestActionOutput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.FunctionalTestConditionOutput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.FunctionalTestMethodSignatureOutput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.GenerateFunctionalTestSignatureOutput;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
                        List.of(new FunctionalTestConditionOutput(conditionId, "C1", "condicao", DecisionTableConditionValueEnum.NO)),
                        List.of(new FunctionalTestActionOutput(actionId, "E1", "acao", DecisionTableActionValueEnum.YES)),
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
        assertEquals(List.of("Aviso"), dto.warnings());
        assertEquals(code, dto.generatedCode());
    }
}
