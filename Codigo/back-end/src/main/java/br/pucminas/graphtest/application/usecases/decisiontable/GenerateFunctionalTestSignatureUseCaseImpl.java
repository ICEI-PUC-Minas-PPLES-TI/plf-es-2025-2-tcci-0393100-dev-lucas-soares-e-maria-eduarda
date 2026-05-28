package br.pucminas.graphtest.application.usecases.decisiontable;

import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableSyncStatusEnum;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTable;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTableAction;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTableActionCell;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTableCondition;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTableConditionCell;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTableRule;
import br.pucminas.graphtest.application.exception.DecisionTableHasNoRulesException;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.decisiontable.GenerateFunctionalTestSignatureUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.records.FunctionalTestActionOutput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.FunctionalTestConditionOutput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.FunctionalTestMethodSignatureOutput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.GenerateFunctionalTestSignatureOutput;
import br.pucminas.graphtest.application.port.output.repositories.DecisionTableRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class GenerateFunctionalTestSignatureUseCaseImpl implements GenerateFunctionalTestSignatureUseCasePort {

    private static final String DECISION_TABLE_NOT_FOUND_MESSAGE = "Tabela de decisao nao encontrada";
    private static final String DECISION_TABLE_WITHOUT_RULES_MESSAGE = "A tabela de decisao deve possuir ao menos uma regra para gerar assinaturas de teste funcional.";
    private static final String STALE_DECISION_TABLE_WARNING = "A tabela de decisao esta desatualizada em relacao ao GCE de origem. Recomenda-se sincroniza-la antes de utilizar os testes funcionais gerados.";

    private final DecisionTableRepositoryPort decisionTableRepositoryPort;
    private final ProjectAccessService projectAccessService;

    public GenerateFunctionalTestSignatureUseCaseImpl(
            DecisionTableRepositoryPort decisionTableRepositoryPort,
            ProjectAccessService projectAccessService
    ) {
        this.decisionTableRepositoryPort = decisionTableRepositoryPort;
        this.projectAccessService = projectAccessService;
    }

    @Override
    public GenerateFunctionalTestSignatureOutput execute(UUID decisionTableId) {
        DecisionTable decisionTable = decisionTableRepositoryPort.findById(decisionTableId)
                .orElseThrow(() -> new EntityNotFoundException(DECISION_TABLE_NOT_FOUND_MESSAGE));
        projectAccessService.findAuthorizedProject(decisionTable.getProjectId());

        List<DecisionTableRule> rules = decisionTable.getRules().stream()
                .sorted(Comparator.comparingInt(DecisionTableRule::getOrderIndex))
                .toList();
        validateRules(rules);

        List<DecisionTableCondition> conditions = decisionTable.getConditions().stream()
                .sorted(Comparator.comparingInt(DecisionTableCondition::getOrderIndex))
                .toList();
        List<DecisionTableAction> actions = decisionTable.getActions().stream()
                .sorted(Comparator.comparingInt(DecisionTableAction::getOrderIndex))
                .toList();

        List<FunctionalTestMethodSignatureOutput> testMethods = IntStream.range(0, rules.size())
                .mapToObj(index -> buildMethod(decisionTable, rules.get(index), index + 1, conditions, actions))
                .toList();
        String generatedCode = testMethods.stream()
                .map(FunctionalTestMethodSignatureOutput::generatedCode)
                .reduce((first, second) -> first + "\n\n" + second)
                .orElse("");

        return new GenerateFunctionalTestSignatureOutput(
                decisionTable.getId(),
                decisionTable.getGceId(),
                decisionTable.getProjectId(),
                decisionTable.getName(),
                rules.size(),
                testMethods,
                generatedCode,
                buildWarnings(decisionTable)
        );
    }

    private void validateRules(List<DecisionTableRule> rules) {
        if (rules.isEmpty()) {
            throw new DecisionTableHasNoRulesException(DECISION_TABLE_WITHOUT_RULES_MESSAGE);
        }
    }

    private FunctionalTestMethodSignatureOutput buildMethod(
            DecisionTable decisionTable,
            DecisionTableRule rule,
            int methodIndex,
            List<DecisionTableCondition> conditions,
            List<DecisionTableAction> actions
    ) {
        String methodName = "testeFuncional%02d".formatted(methodIndex);
        return new FunctionalTestMethodSignatureOutput(
                rule.getId(),
                rule.getCode(),
                methodName,
                conditions.stream()
                        .map(condition -> mapCondition(decisionTable, rule, condition))
                        .filter(java.util.Objects::nonNull)
                        .toList(),
                actions.stream()
                        .map(action -> mapAction(decisionTable, rule, action))
                        .filter(java.util.Objects::nonNull)
                        .toList(),
                "@Test\nvoid " + methodName + "() {\n\n}"
        );
    }

    private FunctionalTestConditionOutput mapCondition(
            DecisionTable decisionTable,
            DecisionTableRule rule,
            DecisionTableCondition condition
    ) {
        DecisionTableConditionCell cell = decisionTable.findConditionCell(rule.getId(), condition.getId());
        return cell == null ? null : new FunctionalTestConditionOutput(
                condition.getId(),
                condition.getCode(),
                condition.getLabel(),
                cell.getValue()
        );
    }

    private FunctionalTestActionOutput mapAction(
            DecisionTable decisionTable,
            DecisionTableRule rule,
            DecisionTableAction action
    ) {
        DecisionTableActionCell cell = decisionTable.findActionCell(rule.getId(), action.getId());
        return cell == null ? null : new FunctionalTestActionOutput(
                action.getId(),
                action.getCode(),
                action.getLabel(),
                cell.getValue()
        );
    }

    private List<String> buildWarnings(DecisionTable decisionTable) {
        return decisionTable.getSyncStatus() == DecisionTableSyncStatusEnum.STALE
                ? List.of(STALE_DECISION_TABLE_WARNING)
                : List.of();
    }
}
