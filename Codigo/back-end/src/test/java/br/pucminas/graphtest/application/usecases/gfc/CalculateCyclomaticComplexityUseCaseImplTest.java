package br.pucminas.graphtest.application.usecases.gfc;

import br.pucminas.graphtest.application.domain.gfc.enums.GfcEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.gfc.enums.GfcNodeTypeEnum;
import br.pucminas.graphtest.application.domain.gfc.model.Gfc;
import br.pucminas.graphtest.application.domain.gfc.model.GfcEdge;
import br.pucminas.graphtest.application.domain.gfc.model.GfcNode;
import br.pucminas.graphtest.application.exception.GfcNotFoundException;
import br.pucminas.graphtest.application.port.input.gfc.records.CyclomaticComplexityOutput;
import br.pucminas.graphtest.application.port.output.repositories.GfcRepositoryPort;
import br.pucminas.graphtest.application.service.gfc.GfcGenerationServiceImpl;
import br.pucminas.graphtest.application.service.gfc.parser.JavaSourceParser;
import br.pucminas.graphtest.application.service.gfc.records.GfcGenerationInput;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculateCyclomaticComplexityUseCaseImplTest {

    private static final UUID PROJECT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID SOURCE_FILE_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final String INCONSISTENT_FORMULAS_WARNING = "Os valores calculados pelas fórmulas V(g) = a - n + 2 e V(G) = P + 1 são diferentes. Recomenda-se revisar a estrutura do grafo, pois pode haver inconsistência na modelagem dos nós ou arestas.";
    private static final String COMPLEXITY_NORMAL_WARNING = "A complexidade ciclomática é menor que 10. Isso indica baixa complexidade.";
    private static final String COMPLEXITY_GREATER_THAN_TEN_WARNING = "A complexidade ciclomática é maior que 10. Isso pode indicar um método difícil de testar, compreender e manter. Recomenda-se avaliar o redesenho ou refatoração do método.";
    private static final String COMPLEXITY_GREATER_THAN_FIFTEEN_WARNING = "A complexidade ciclomática é maior que 15. O método possui alta complexidade e está além do limite aceitável, sendo fortemente recomendada sua refatoração.";

    @Mock
    private GfcRepositoryPort gfcRepositoryPort;

    @Mock
    private ProjectAccessService projectAccessService;

    @InjectMocks
    private CalculateCyclomaticComplexityUseCaseImpl useCase;

    @Test
    void shouldCalculateComplexityOneForSimpleMethodWithoutDecision() {
        Gfc gfc = generate("""
                public class Exemplo {
                    void executar() {
                        int x = 1;
                    }
                }
                """);

        CyclomaticComplexityOutput output = executeFor(gfc);

        assertEquals(2, output.nodesCount());
        assertEquals(1, output.edgesCount());
        assertEquals(0, output.predicateNodesCount());
        assertEquals(1, output.cyclomaticComplexityByEdgesAndNodes());
        assertEquals(1, output.cyclomaticComplexityByPredicateNodes());
        assertEquals(List.of(COMPLEXITY_NORMAL_WARNING), output.warnings());
    }

    @Test
    void shouldCalculateComplexityTwoForSimpleIf() {
        Gfc gfc = generate("""
                public class Exemplo {
                    void executar(int x) {
                        if (x > 0) {
                            System.out.println("positivo");
                        }
                        System.out.println("fim");
                    }
                }
                """);

        CyclomaticComplexityOutput output = executeFor(gfc);

        assertEquals(4, output.nodesCount());
        assertEquals(4, output.edgesCount());
        assertEquals(1, output.predicateNodesCount());
        assertEquals(2, output.cyclomaticComplexityByEdgesAndNodes());
        assertEquals(2, output.cyclomaticComplexityByPredicateNodes());
        assertEquals(List.of(COMPLEXITY_NORMAL_WARNING), output.warnings());
    }

    @Test
    void shouldCalculateComplexityTwoForLoop() {
        Gfc gfc = generate("""
                public class Exemplo {
                    void executar() {
                        while (ativo()) {
                            trabalhar();
                        }
                    }
                }
                """);

        CyclomaticComplexityOutput output = executeFor(gfc);

        assertEquals(1, output.predicateNodesCount());
        assertEquals(2, output.cyclomaticComplexityByEdgesAndNodes());
        assertEquals(2, output.cyclomaticComplexityByPredicateNodes());
        assertEquals(List.of(COMPLEXITY_NORMAL_WARNING), output.warnings());
    }

    @Test
    void shouldCountSwitchPredicateByBranchQuantityMinusOne() {
        Gfc gfc = generate("""
                public class Exemplo {
                    void executar(int numero) {
                        switch (numero) {
                            case 1:
                                executarA();
                                break;
                            case 2:
                                executarB();
                                break;
                            default:
                                executarPadrao();
                        }
                    }
                }
                """);

        CyclomaticComplexityOutput output = executeFor(gfc);

        assertEquals(3, switchBranchCount(gfc));
        assertEquals(2, output.predicateNodesCount());
        assertEquals(3, output.cyclomaticComplexityByPredicateNodes());
        assertEquals(List.of(COMPLEXITY_NORMAL_WARNING), output.warnings());
    }

    @Test
    void shouldCalculateProcessarNumeroSwitchAndPreserveGroupedCasesAndFallThrough() {
        Gfc gfc = generate("""
                public class Exemplo {
                    public void processarNumero(int numero) {
                        switch (numero) {
                            case 1:
                                System.out.println("Numero um");
                                break;
                            case 2:
                                System.out.println("Numero dois");
                                break;
                            case 3:
                            case 4:
                                System.out.println("Numero tres ou quatro");
                                break;
                            case 5:
                                System.out.println("Numero cinco");
                            case 6:
                                System.out.println("Numero seis");
                                break;
                            default:
                                System.out.println("Numero desconhecido");
                        }
                        System.out.println("Fim do processamento do numero");
                    }
                }
                """);

        CyclomaticComplexityOutput output = executeFor(gfc);

        String groupedCaseBlockCode = gfc.getEdges().stream()
                .filter(edge -> edge.getType() == GfcEdgeTypeEnum.CASE_BRANCH)
                .filter(edge -> edge.getLabel().contains("case 3"))
                .findFirst()
                .orElseThrow()
                .getTargetNodeCode();
        assertTrue(gfc.getEdges().stream().anyMatch(edge -> edge.getType() == GfcEdgeTypeEnum.CASE_BRANCH
                && edge.getLabel().contains("case 4")
                && edge.getTargetNodeCode().equals(groupedCaseBlockCode)));

        String case5BlockCode = gfc.getEdges().stream()
                .filter(edge -> edge.getType() == GfcEdgeTypeEnum.CASE_BRANCH)
                .filter(edge -> edge.getLabel().contains("case 5"))
                .findFirst()
                .orElseThrow()
                .getTargetNodeCode();
        assertTrue(gfc.getEdges().stream().anyMatch(edge -> edge.getSourceNodeCode().equals(case5BlockCode)
                && edge.getType() == GfcEdgeTypeEnum.SEQUENTIAL
                && gfc.findNode(edge.getTargetNodeCode()).orElseThrow().getLabel().contains("Numero seis")));

        assertEquals(7, switchBranchCount(gfc));
        assertEquals(9, output.nodesCount());
        assertEquals(14, output.edgesCount());
        assertEquals(6, output.predicateNodesCount());
        assertEquals(7, output.cyclomaticComplexityByEdgesAndNodes());
        assertEquals(output.cyclomaticComplexityByEdgesAndNodes(), output.cyclomaticComplexityByPredicateNodes());
        assertEquals(List.of(COMPLEXITY_NORMAL_WARNING), output.warnings());
    }

    @Test
    void shouldWarnWhenFormulasReturnDifferentValues() {
        Gfc gfc = graphWithOneDecisionAndExtraParallelEdge();

        CyclomaticComplexityOutput output = executeFor(gfc);

        assertEquals(3, output.cyclomaticComplexityByEdgesAndNodes());
        assertEquals(2, output.cyclomaticComplexityByPredicateNodes());
        assertEquals(List.of(INCONSISTENT_FORMULAS_WARNING, COMPLEXITY_NORMAL_WARNING), output.warnings());
    }

    @Test
    void shouldWarnWhenComplexityIsGreaterThanTenAndLessThanOrEqualToFifteen() {
        Gfc gfc = linearGraphWithEdgesCountMinusNodesCount(10);

        CyclomaticComplexityOutput output = executeFor(gfc);

        assertEquals(12, output.cyclomaticComplexityByEdgesAndNodes());
        assertEquals(List.of(INCONSISTENT_FORMULAS_WARNING, COMPLEXITY_GREATER_THAN_TEN_WARNING), output.warnings());
    }

    @Test
    void shouldWarnOnlyWithSevereComplexityMessageWhenComplexityIsGreaterThanFifteen() {
        Gfc gfc = linearGraphWithEdgesCountMinusNodesCount(17);

        CyclomaticComplexityOutput output = executeFor(gfc);

        assertEquals(19, output.cyclomaticComplexityByEdgesAndNodes());
        assertEquals(List.of(INCONSISTENT_FORMULAS_WARNING, COMPLEXITY_GREATER_THAN_FIFTEEN_WARNING), output.warnings());
    }

    @Test
    void shouldNotCountTryFinallyAsPredicateNodes() {
        Gfc gfc = generate("""
                public class Exemplo {
                    void executar() {
                        try {
                            processar();
                        } finally {
                            liberar();
                        }
                    }
                }
                """);

        CyclomaticComplexityOutput output = executeFor(gfc);

        assertTrue(gfc.getNodes().stream().anyMatch(node -> node.getType() == GfcNodeTypeEnum.TRY));
        assertTrue(gfc.getNodes().stream().anyMatch(node -> node.getType() == GfcNodeTypeEnum.FINALLY));
        assertEquals(0, output.predicateNodesCount());
        assertEquals(1, output.cyclomaticComplexityByPredicateNodes());
    }

    @Test
    void shouldCountOneCatchAsPredicateNode() {
        Gfc gfc = generate("""
                public class Exemplo {
                    void executar() {
                        try {
                            processar();
                        } catch (IllegalArgumentException ex) {
                            tratarErro();
                        }
                    }
                }
                """);

        CyclomaticComplexityOutput output = executeFor(gfc);

        assertEquals(1, gfc.getNodes().stream().filter(node -> node.getType() == GfcNodeTypeEnum.CATCH).count());
        assertEquals(1, output.predicateNodesCount());
        assertEquals(2, output.cyclomaticComplexityByPredicateNodes());
    }

    @Test
    void shouldCountMultipleCatchBlocksAsPredicateNodes() {
        Gfc gfc = generate("""
                import java.io.IOException;
                import java.sql.SQLException;

                public class Exemplo {
                    void executar() {
                        try {
                            processarPedido();
                        } catch (IOException ex) {
                            tratarErroIO();
                        } catch (SQLException ex) {
                            tratarErroBanco();
                        }
                    }
                }
                """);

        CyclomaticComplexityOutput output = executeFor(gfc);

        assertEquals(2, gfc.getNodes().stream().filter(node -> node.getType() == GfcNodeTypeEnum.CATCH).count());
        assertEquals(2, output.predicateNodesCount());
        assertEquals(3, output.cyclomaticComplexityByPredicateNodes());
    }

    @Test
    void shouldCountCatchButNotTryOrFinallyAsPredicateNode() {
        Gfc gfc = generate("""
                import java.io.IOException;

                public class Exemplo {
                    void executar() {
                        try {
                            processarPedido();
                        } catch (IOException ex) {
                            tratarErroIO();
                        } finally {
                            liberarRecursos();
                        }
                    }
                }
                """);

        CyclomaticComplexityOutput output = executeFor(gfc);

        assertTrue(gfc.getNodes().stream().anyMatch(node -> node.getType() == GfcNodeTypeEnum.TRY));
        assertTrue(gfc.getNodes().stream().anyMatch(node -> node.getType() == GfcNodeTypeEnum.FINALLY));
        assertEquals(1, gfc.getNodes().stream().filter(node -> node.getType() == GfcNodeTypeEnum.CATCH).count());
        assertEquals(1, output.predicateNodesCount());
        assertEquals(2, output.cyclomaticComplexityByPredicateNodes());
    }

    @Test
    void shouldCalculateConsistentComplexityForTryCatchFinallyWithoutDirectTryToFinallyEdge() {
        Gfc gfc = generate("""
                public class Exemplo {
                    public static int aplicarValidacaoEBonus(
                            int pontuacao,
                            double valor,
                            String categoria
                    ) {
                        try {
                            validarValor(valor);
                            pontuacao += calcularBonus(categoria);
                        } catch (IllegalArgumentException ex) {
                            pontuacao = -100;
                        } finally {
                            pontuacao += 1;
                        }

                        return pontuacao;
                    }
                }
                """);

        CyclomaticComplexityOutput output = executeFor(gfc);

        assertFalse(hasDirectTryToFinallyEdge(gfc));
        assertEquals(9, output.nodesCount());
        assertEquals(9, output.edgesCount());
        assertEquals(1, output.predicateNodesCount());
        assertEquals(2, output.cyclomaticComplexityByEdgesAndNodes());
        assertEquals(2, output.cyclomaticComplexityByPredicateNodes());
        assertEquals(List.of(COMPLEXITY_NORMAL_WARNING), output.warnings());
    }

    @Test
    void shouldThrowNotFoundWhenGfcDoesNotExist() {
        UUID gfcId = UUID.randomUUID();
        when(gfcRepositoryPort.findById(gfcId)).thenReturn(Optional.empty());

        assertThrows(GfcNotFoundException.class, () -> useCase.execute(gfcId));
        verifyNoInteractions(projectAccessService);
    }

    private CyclomaticComplexityOutput executeFor(Gfc gfc) {
        when(gfcRepositoryPort.findById(gfc.getId())).thenReturn(Optional.of(gfc));

        CyclomaticComplexityOutput output = useCase.execute(gfc.getId());

        verify(projectAccessService).findAuthorizedProject(gfc.getProjectId());
        assertEquals(gfc.getId(), output.gfcId());
        assertEquals(countNodesForFormula(gfc), output.nodesCount());
        assertEquals(countEdgesForFormula(gfc), output.edgesCount());
        assertEquals("V(g) = a - n + 2", output.formulaByEdgesAndNodes());
        assertEquals("V(G) = P + 1", output.formulaByPredicateNodes());
        return output;
    }

    private Gfc generate(String sourceCode) {
        JavaSourceParser parser = new JavaSourceParser();
        String methodSignature = parser.listMethods(sourceCode).getFirst().signature();
        return new GfcGenerationServiceImpl(parser).generate(new GfcGenerationInput(
                PROJECT_ID,
                SOURCE_FILE_ID,
                sourceCode,
                methodSignature,
                "GFC",
                null
        ));
    }

    private long switchBranchCount(Gfc gfc) {
        String switchNodeCode = gfc.getNodes().stream()
                .filter(node -> node.getType() == GfcNodeTypeEnum.SWITCH)
                .findFirst()
                .orElseThrow()
                .getCode();
        return gfc.getEdges().stream()
                .filter(edge -> edge.getSourceNodeCode().equals(switchNodeCode))
                .filter(edge -> edge.getType() == GfcEdgeTypeEnum.CASE_BRANCH
                        || edge.getType() == GfcEdgeTypeEnum.DEFAULT_BRANCH)
                .count();
    }

    private int countNodesForFormula(Gfc gfc) {
        return (int) gfc.getNodes().stream()
                .filter(node -> node.getType() != GfcNodeTypeEnum.START)
                .count();
    }

    private int countEdgesForFormula(Gfc gfc) {
        String startNodeCode = gfc.getNodes().stream()
                .filter(node -> node.getType() == GfcNodeTypeEnum.START)
                .findFirst()
                .map(GfcNode::getCode)
                .orElse(null);
        return (int) gfc.getEdges().stream()
                .filter(edge -> startNodeCode == null || !edge.startsFrom(startNodeCode))
                .count();
    }

    private boolean hasDirectTryToFinallyEdge(Gfc graph) {
        return graph.getEdges().stream().anyMatch(edge ->
                graph.findNode(edge.getSourceNodeCode()).orElseThrow().getType() == GfcNodeTypeEnum.TRY
                        && graph.findNode(edge.getTargetNodeCode()).orElseThrow().getType() == GfcNodeTypeEnum.FINALLY
        );
    }

    private Gfc graphWithOneDecisionAndExtraParallelEdge() {
        GfcNode start = GfcNode.start(UUID.randomUUID(), "N0", "Inicio");
        GfcNode decision = GfcNode.decision(UUID.randomUUID(), "N1", "if (x > 0)", 1, 1);
        GfcNode thenNode = GfcNode.statement(UUID.randomUUID(), "N2", "positivo();", 2, 2);
        GfcNode end = GfcNode.end(UUID.randomUUID(), "N_END", "Fim");

        return Gfc.persisted(
                UUID.randomUUID(),
                PROJECT_ID,
                SOURCE_FILE_ID,
                "void executar(int x)",
                "GFC",
                null,
                "Java",
                List.of(start, decision, thenNode, end),
                List.of(
                        new GfcEdge(UUID.randomUUID(), "N0", "N1", GfcEdgeTypeEnum.SEQUENTIAL, null),
                        new GfcEdge(UUID.randomUUID(), "N1", "N2", GfcEdgeTypeEnum.TRUE_BRANCH, "true"),
                        new GfcEdge(UUID.randomUUID(), "N1", "N_END", GfcEdgeTypeEnum.FALSE_BRANCH, "false"),
                        new GfcEdge(UUID.randomUUID(), "N2", "N_END", GfcEdgeTypeEnum.SEQUENTIAL, null),
                        new GfcEdge(UUID.randomUUID(), "N2", "N_END", GfcEdgeTypeEnum.THROW_FLOW, "throw")
                )
        );
    }

    private Gfc linearGraphWithEdgesCountMinusNodesCount(int difference) {
        GfcNode start = GfcNode.start(UUID.randomUUID(), "N0", "Inicio");
        GfcNode end = GfcNode.end(UUID.randomUUID(), "N_END", "Fim");
        GfcNode statement = GfcNode.statement(UUID.randomUUID(), "N1", "executar();", 1, 1);

        List<GfcEdge> edges = new java.util.ArrayList<>();
        edges.add(new GfcEdge(UUID.randomUUID(), "N0", "N1", GfcEdgeTypeEnum.SEQUENTIAL, null));
        edges.add(new GfcEdge(UUID.randomUUID(), "N1", "N_END", GfcEdgeTypeEnum.SEQUENTIAL, null));
        for (int index = 0; index < difference + 1; index++) {
            edges.add(new GfcEdge(UUID.randomUUID(), "N1", "N_END", GfcEdgeTypeEnum.THROW_FLOW, "throw-" + index));
        }

        return Gfc.persisted(
                UUID.randomUUID(),
                PROJECT_ID,
                SOURCE_FILE_ID,
                "void executar()",
                "GFC",
                null,
                "Java",
                List.of(start, statement, end),
                edges
        );
    }
}
