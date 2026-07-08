package br.pucminas.graphtest.application.port.input.decisiontable.records;

import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableCellValueEnum;
import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableElementEnum;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTableCell;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DecisionTableActionCellOutputTest {

    @Test
    void shouldConvertActionCellToOutput() {
        UUID cellId = UUID.randomUUID();
        UUID ruleId = UUID.randomUUID();
        UUID actionId = UUID.randomUUID();
        DecisionTableCell cell = new DecisionTableCell(cellId, ruleId, actionId, DecisionTableElementEnum.ACTION, DecisionTableCellValueEnum.YES);

        DecisionTableActionCellOutput output = DecisionTableActionCellOutput.from(cell);

        assertEquals(cellId, output.id());
        assertEquals(ruleId, output.ruleId());
        assertEquals(actionId, output.actionId());
        assertEquals(DecisionTableCellValueEnum.YES, output.value());
    }

    @Test
    void shouldThrowWhenCellIsNotAction() {
        DecisionTableCell conditionCell = new DecisionTableCell(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                DecisionTableElementEnum.CONDITION, DecisionTableCellValueEnum.NO
        );

        assertThrows(IllegalArgumentException.class, () -> DecisionTableActionCellOutput.from(conditionCell));
    }
}
