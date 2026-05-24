package br.pucminas.graphtest.application.usecases.gfc;

import br.pucminas.graphtest.application.domain.gfc.enums.GfcEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.gfc.enums.GfcNodeTypeEnum;
import br.pucminas.graphtest.application.domain.gfc.model.Gfc;
import br.pucminas.graphtest.application.domain.gfc.model.GfcEdge;
import br.pucminas.graphtest.application.domain.gfc.model.GfcNode;
import br.pucminas.graphtest.application.exception.GfcNotFoundException;
import br.pucminas.graphtest.application.port.input.gfc.CalculateCyclomaticComplexityUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.records.CyclomaticComplexityOutput;
import br.pucminas.graphtest.application.port.output.repositories.GfcRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CalculateCyclomaticComplexityUseCaseImpl implements CalculateCyclomaticComplexityUseCasePort {

    private static final String FORMULA_BY_EDGES_AND_NODES = "V(G) = e - n + 2";
    private static final String FORMULA_BY_PREDICATE_NODES = "V(G) = P + 1";
    private static final String INCONSISTENT_FORMULAS_WARNING = "Os valores calculados pelas fórmulas V(G) = e - n + 2 e V(G) = P + 1 são diferentes. Recomenda-se revisar a estrutura do grafo, pois pode haver inconsistência na modelagem dos nós ou arestas.";
    private static final String COMPLEXITY_GREATER_THAN_TEN_WARNING = "A complexidade ciclomática é maior que 10. Isso pode indicar um método difícil de testar, compreender e manter. Recomenda-se avaliar o redesenho ou refatoração do método.";
    private static final String COMPLEXITY_GREATER_THAN_FIFTEEN_WARNING = "A complexidade ciclomática é maior que 15. O método possui alta complexidade e está além do limite aceitável, sendo fortemente recomendada sua refatoração.";

    private final GfcRepositoryPort gfcRepositoryPort;
    private final ProjectAccessService projectAccessService;

    public CalculateCyclomaticComplexityUseCaseImpl(GfcRepositoryPort gfcRepositoryPort,
                                                    ProjectAccessService projectAccessService) {
        this.gfcRepositoryPort = gfcRepositoryPort;
        this.projectAccessService = projectAccessService;
    }

    @Override
    public CyclomaticComplexityOutput execute(UUID gfcId) {
        Gfc gfc = gfcRepositoryPort.findById(gfcId)
                .orElseThrow(GfcNotFoundException::new);
        projectAccessService.findAuthorizedProject(gfc.getProjectId());

        String startNodeCode = findStartNodeCode(gfc);
        int nodesCount = countNodesForEdgesAndNodesFormula(gfc);
        int edgesCount = countEdgesForEdgesAndNodesFormula(gfc, startNodeCode);
        int predicateNodesCount = countPredicateNodes(gfc);
        int complexityByEdgesAndNodes = edgesCount - nodesCount + 2;
        int complexityByPredicateNodes = predicateNodesCount + 1;

        return new CyclomaticComplexityOutput(
                gfc.getId(),
                nodesCount,
                edgesCount,
                predicateNodesCount,
                complexityByEdgesAndNodes,
                complexityByPredicateNodes,
                FORMULA_BY_EDGES_AND_NODES,
                FORMULA_BY_PREDICATE_NODES,
                buildWarnings(complexityByEdgesAndNodes, complexityByPredicateNodes)
        );
    }

    private List<String> buildWarnings(int complexityByEdgesAndNodes, int complexityByPredicateNodes) {
        List<String> warnings = new ArrayList<>();

        if (complexityByEdgesAndNodes != complexityByPredicateNodes) {
            warnings.add(INCONSISTENT_FORMULAS_WARNING);
        }

        if (complexityByEdgesAndNodes > 15) {
            warnings.add(COMPLEXITY_GREATER_THAN_FIFTEEN_WARNING);
        } else if (complexityByEdgesAndNodes > 10) {
            warnings.add(COMPLEXITY_GREATER_THAN_TEN_WARNING);
        }

        return warnings;
    }

    private String findStartNodeCode(Gfc gfc) {
        return gfc.getNodes().stream()
                .filter(node -> node.getType() == GfcNodeTypeEnum.START)
                .findFirst()
                .map(GfcNode::getCode)
                .orElse(null);
    }

    private int countNodesForEdgesAndNodesFormula(Gfc gfc) {
        return (int) gfc.getNodes().stream()
                .filter(node -> node.getType() != GfcNodeTypeEnum.START)
                .count();
    }

    private int countEdgesForEdgesAndNodesFormula(Gfc gfc, String startNodeCode) {
        return (int) gfc.getEdges().stream()
                .filter(edge -> startNodeCode == null || !edge.startsFrom(startNodeCode))
                .count();
    }

    private int countPredicateNodes(Gfc gfc) {
        int total = 0;

        for (GfcNode node : gfc.getNodes()) {
            total += predicateContribution(gfc, node);
        }

        return total;
    }

    private int predicateContribution(Gfc gfc, GfcNode node) {
        GfcNodeTypeEnum type = node.getType();
        if (type == GfcNodeTypeEnum.DECISION
                || type == GfcNodeTypeEnum.LOOP
                || type == GfcNodeTypeEnum.TERNARY
                || type == GfcNodeTypeEnum.CATCH) {
            return 1;
        }
        if (type == GfcNodeTypeEnum.SWITCH) {
            long branches = gfc.getEdges().stream()
                    .filter(edge -> edge.startsFrom(node.getCode()))
                    .filter(this::isSwitchBranch)
                    .count();
            return branches > 0 ? (int) branches - 1 : 0;
        }
        return 0;
    }

    private boolean isSwitchBranch(GfcEdge edge) {
        return edge.getType() == GfcEdgeTypeEnum.CASE_BRANCH
                || edge.getType() == GfcEdgeTypeEnum.DEFAULT_BRANCH;
    }
}
