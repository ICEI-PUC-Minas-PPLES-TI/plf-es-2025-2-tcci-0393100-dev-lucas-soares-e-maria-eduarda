package br.pucminas.graphtest.application.usecases.decisiontable;

import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableCellValueEnum;
import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableElementEnum;
import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableSyncStatusEnum;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTable;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTableCell;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTableElement;
import br.pucminas.graphtest.application.exception.DecisionTableHasNoRulesException;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.exception.UnauthorizedUserException;
import br.pucminas.graphtest.application.port.input.decisiontable.records.GenerateFunctionalTestSignatureOutput;
import br.pucminas.graphtest.application.port.output.repositories.DecisionTableRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerateFunctionalTestSignatureUseCaseImplTest {

    private static final UUID TABLE_ID = UUID.fromString("00000000-0000-0000-0000-000000000101");
    private static final UUID GCE_ID = UUID.fromString("00000000-0000-0000-0000-000000000102");
    private static final UUID PROJECT_ID = UUID.fromString("00000000-0000-0000-0000-000000000103");
    private static final String STALE_WARNING = "A tabela de decisao esta desatualizada em relacao ao GCE de origem. Recomenda-se sincroniza-la antes de utilizar os testes funcionais gerados.";

    @Mock
    private DecisionTableRepositoryPort decisionTableRepositoryPort;

    @Mock
    private ProjectAccessService projectAccessService;

    @InjectMocks
    private GenerateFunctionalTestSignatureUseCaseImpl useCase;

    @Test
    void shouldGenerateOneEmptyFunctionalTestForOneRule() {
        GenerateFunctionalTestSignatureOutput output = executeFor(tableWithRules(1, DecisionTableSyncStatusEnum.UP_TO_DATE));

        assertEquals(TABLE_ID, output.decisionTableId());
        assertEquals(GCE_ID, output.gceId());
        assertEquals(PROJECT_ID, output.projectId());
        assertEquals("Tabela Login", output.decisionTableName());
        assertEquals(1, output.rulesCount());
        assertEquals("testeFuncional01", output.testMethods().getFirst().methodName());
        assertEquals("@Test\nvoid testeFuncional01() {\n\n}", output.generatedCode());
        assertTrue(output.warnings().isEmpty());
    }

    @Test
    void shouldGenerateMethodsAndTraceabilityOrderedByOrderIndexForThreeRules() {
        GenerateFunctionalTestSignatureOutput output = executeFor(tableWithRules(3, DecisionTableSyncStatusEnum.UP_TO_DATE));

        assertEquals(List.of("R1", "R2", "R3"),
                output.testMethods().stream().map(method -> method.ruleCode()).toList());
        assertEquals(List.of("testeFuncional01", "testeFuncional02", "testeFuncional03"),
                output.testMethods().stream().map(method -> method.methodName()).toList());
        assertEquals(List.of("C1", "C2"),
                output.testMethods().getFirst().conditions().stream().map(condition -> condition.code()).toList());
        assertEquals(List.of("E1", "E2"),
                output.testMethods().getFirst().actions().stream().map(action -> action.code()).toList());
        assertEquals(DecisionTableCellValueEnum.NO, output.testMethods().getFirst().conditions().getFirst().value());
        assertEquals(DecisionTableCellValueEnum.YES, output.testMethods().getFirst().actions().getFirst().value());
        assertEquals(3, output.generatedCode().lines().filter("@Test"::equals).count());
        assertTrue(output.generatedCode().contains("}\n\n@Test\nvoid testeFuncional02() {\n\n}"));
        assertTrue(output.testMethods().stream().allMatch(method -> method.generatedCode().endsWith("{\n\n}")));
    }

    @Test
    void shouldFormatFunctionalMethodNamesForTenRules() {
        GenerateFunctionalTestSignatureOutput output = executeFor(tableWithRules(10, DecisionTableSyncStatusEnum.UP_TO_DATE));

        assertEquals(10, output.testMethods().size());
        assertEquals("testeFuncional01", output.testMethods().getFirst().methodName());
        assertEquals("testeFuncional09", output.testMethods().get(8).methodName());
        assertEquals("testeFuncional10", output.testMethods().get(9).methodName());
    }

    @Test
    void shouldAllowGenerationAndWarnWhenDecisionTableIsStale() {
        GenerateFunctionalTestSignatureOutput output = executeFor(tableWithRules(1, DecisionTableSyncStatusEnum.STALE));

        assertEquals(List.of(STALE_WARNING), output.warnings());
        assertEquals(1, output.testMethods().size());
    }

    @Test
    void shouldThrowNotFoundWhenDecisionTableDoesNotExist() {
        when(decisionTableRepositoryPort.findById(TABLE_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> useCase.execute(PROJECT_ID, TABLE_ID));

        verifyNoInteractions(projectAccessService);
    }

    @Test
    void shouldPropagateAuthorizationFailure() {
        DecisionTable table = tableWithRules(1, DecisionTableSyncStatusEnum.UP_TO_DATE);
        when(decisionTableRepositoryPort.findById(TABLE_ID)).thenReturn(Optional.of(table));
        doThrow(new UnauthorizedUserException("Sem permissao"))
                .when(projectAccessService).findAuthorizedProject(PROJECT_ID);

        assertThrows(UnauthorizedUserException.class, () -> useCase.execute(PROJECT_ID, TABLE_ID));
    }

    @Test
    void shouldRejectDecisionTableWithoutRules() {
        DecisionTable table = tableWithRules(0, DecisionTableSyncStatusEnum.UP_TO_DATE);
        when(decisionTableRepositoryPort.findById(TABLE_ID)).thenReturn(Optional.of(table));

        assertThrows(DecisionTableHasNoRulesException.class, () -> useCase.execute(PROJECT_ID, TABLE_ID));
    }

    @Test
    void shouldThrowNotFoundWhenDecisionTableDoesNotBelongToProject() {
        DecisionTable table = tableWithRules(1, DecisionTableSyncStatusEnum.UP_TO_DATE);
        UUID wrongProjectId = UUID.randomUUID();
        when(decisionTableRepositoryPort.findById(TABLE_ID)).thenReturn(Optional.of(table));

        assertThrows(EntityNotFoundException.class, () -> useCase.execute(wrongProjectId, TABLE_ID));
        verify(projectAccessService).findAuthorizedProject(PROJECT_ID);
    }

    private GenerateFunctionalTestSignatureOutput executeFor(DecisionTable table) {
        when(decisionTableRepositoryPort.findById(TABLE_ID)).thenReturn(Optional.of(table));

        GenerateFunctionalTestSignatureOutput output = useCase.execute(PROJECT_ID, TABLE_ID);

        verify(projectAccessService).findAuthorizedProject(PROJECT_ID);
        return output;
    }

    private DecisionTable tableWithRules(int rulesCount, DecisionTableSyncStatusEnum syncStatus) {
        DecisionTableElement c1 = new DecisionTableElement(UUID.randomUUID(), TABLE_ID, "C1", "usuario valido", 0, DecisionTableElementEnum.CONDITION);
        DecisionTableElement c2 = new DecisionTableElement(UUID.randomUUID(), TABLE_ID, "C2", "senha valida", 1, DecisionTableElementEnum.CONDITION);
        DecisionTableElement e1 = new DecisionTableElement(UUID.randomUUID(), TABLE_ID, "E1", "permitir login", 0, DecisionTableElementEnum.ACTION);
        DecisionTableElement e2 = new DecisionTableElement(UUID.randomUUID(), TABLE_ID, "E2", "negar login", 1, DecisionTableElementEnum.ACTION);
        List<DecisionTableElement> rules = new ArrayList<>();
        List<DecisionTableCell> cells = new ArrayList<>();

        for (int index = rulesCount; index >= 1; index--) {
            DecisionTableElement rule = new DecisionTableElement(
                    UUID.randomUUID(),
                    TABLE_ID,
                    "R" + index,
                    null,
                    "",
                    index - 1,
                    DecisionTableElementEnum.RULE
            );
            rules.add(rule);
            cells.add(new DecisionTableCell(UUID.randomUUID(), rule.getId(), c2.getId(), DecisionTableElementEnum.CONDITION, DecisionTableCellValueEnum.YES));
            cells.add(new DecisionTableCell(UUID.randomUUID(), rule.getId(), c1.getId(), DecisionTableElementEnum.CONDITION, DecisionTableCellValueEnum.NO));
            cells.add(new DecisionTableCell(UUID.randomUUID(), rule.getId(), e2.getId(), DecisionTableElementEnum.ACTION, DecisionTableCellValueEnum.NO));
            cells.add(new DecisionTableCell(UUID.randomUUID(), rule.getId(), e1.getId(), DecisionTableElementEnum.ACTION, DecisionTableCellValueEnum.YES));
        }

        return new DecisionTable(
                TABLE_ID,
                GCE_ID,
                PROJECT_ID,
                "Tabela Login",
                null,
                "fingerprint",
                syncStatus,
                null,
                joinElements(c2, c1, e2, e1, rules),
                cells
        );
    }

    private List<DecisionTableElement> joinElements(DecisionTableElement c2,
                                                    DecisionTableElement c1,
                                                    DecisionTableElement e2,
                                                    DecisionTableElement e1,
                                                    List<DecisionTableElement> rules) {
        List<DecisionTableElement> elements = new ArrayList<>();
        elements.add(c2);
        elements.add(c1);
        elements.add(e2);
        elements.add(e1);
        elements.addAll(rules);
        return elements;
    }
}
