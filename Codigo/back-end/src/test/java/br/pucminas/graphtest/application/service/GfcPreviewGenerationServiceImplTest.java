package br.pucminas.graphtest.application.service;

import br.pucminas.graphtest.application.domain.gfc.enums.GfcEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.gfc.enums.GfcNodeTypeEnum;
import br.pucminas.graphtest.application.domain.gfc.model.Gfc;
import br.pucminas.graphtest.application.exception.GfcMethodNotFoundException;
import br.pucminas.graphtest.application.exception.InvalidJavaSourceCodeException;
import br.pucminas.graphtest.application.port.input.gfc.records.PreviewGfcInput;
import br.pucminas.graphtest.application.service.gfc.GfcGenerationServiceImpl;
import br.pucminas.graphtest.application.service.gfc.GfcPreviewGenerationServiceImpl;
import br.pucminas.graphtest.application.service.gfc.parser.JavaSourceParser;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static br.pucminas.graphtest.application.domain.gfc.rules.GfcDomainRules.JAVA_LANGUAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GfcPreviewGenerationServiceImplTest {

    private final GfcPreviewGenerationServiceImpl service = new GfcPreviewGenerationServiceImpl(
            new GfcGenerationServiceImpl(new JavaSourceParser())
    );

    @Test
    void shouldGeneratePreviewForFirstMethodWithDecisionAndReturn() {
        UUID projectId = UUID.randomUUID();
        PreviewGfcInput input = new PreviewGfcInput(
                projectId,
                "GFC calcular",
                "Preview",
                """
                        public class Calculadora {
                            int calcular(int valor) {
                                if (valor > 0) {
                                    return valor;
                                }
                                return 0;
                            }
                        }
                        """
                ,
                null
        );

        Gfc graph = service.generate(input);

        assertEquals(projectId, graph.getProjectId());
        assertEquals("GFC calcular", graph.getName());
        assertEquals(JAVA_LANGUAGE, graph.getLanguage());
        assertTrue(graph.getNodes().stream().anyMatch(node -> node.isStart() && node.getCode().equals("N0") && node.getStartLine() == null));
        assertTrue(graph.getNodes().stream().anyMatch(node -> node.isEnd() && node.getCode().equals("N_END") && node.getEndLine() == null));
        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getType() == GfcNodeTypeEnum.DECISION));
        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getType() == GfcNodeTypeEnum.RETURN));
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getType() == GfcEdgeTypeEnum.TRUE_BRANCH));
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getType() == GfcEdgeTypeEnum.FALSE_BRANCH));
    }

    @Test
    void shouldGenerateLoopBackEdgeForLoopStatement() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC loop",
                null,
                """
                        public class Exemplo {
                            void executar() {
                                while (ativo()) {
                                    trabalhar();
                                }
                            }
                        }
                        """
                ,
                null
        );

        Gfc graph = service.generate(input);

        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getType() == GfcNodeTypeEnum.LOOP));
        assertTrue(graph.getNodes().stream().noneMatch(node -> node.getLabel().contains("while (ativo())")
                && node.getType() == GfcNodeTypeEnum.DECISION));
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getType() == GfcEdgeTypeEnum.LOOP_BODY));
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getType() == GfcEdgeTypeEnum.LOOP_EXIT));
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getType() == GfcEdgeTypeEnum.LOOP_BACK));
    }

    @Test
    void shouldGenerateLoopNodesForForAndForEachLoops() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC for",
                null,
                """
                        public class Exemplo {
                            void executar(java.util.List<String> itens) {
                                for (int i = 0; i < 10; i++) {
                                    trabalhar(i);
                                }
                                for (String item : itens) {
                                    consumir(item);
                                }
                            }
                        }
                        """
                ,
                null
        );

        Gfc graph = service.generate(input);

        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getLabel().contains("for (int i = 0; i < 10; i++)")));
        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getLabel().contains("for (String item : itens)")));
        assertEquals(2, graph.getNodes().stream().filter(node -> node.getType() == GfcNodeTypeEnum.LOOP).count());
        assertEquals(2, graph.getEdges().stream().filter(edge -> edge.getType() == GfcEdgeTypeEnum.LOOP_BODY).count());
        assertEquals(2, graph.getEdges().stream().filter(edge -> edge.getType() == GfcEdgeTypeEnum.LOOP_EXIT).count());
        assertEquals(2, graph.getEdges().stream().filter(edge -> edge.getType() == GfcEdgeTypeEnum.LOOP_BACK).count());
    }

    @Test
    void shouldGeneratePreviewFromMethodBodySnippet() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC trecho",
                null,
                """
                        int z = 0;
                        if (x > 0) {
                            z = 1;
                        }
                        return z;
                        """
                ,
                null
        );

        Gfc graph = service.generate(input);

        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getLabel().contains("int z = 0")));
        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getLabel().contains("if (x > 0)")));
        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getType() == GfcNodeTypeEnum.RETURN));
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getType() == GfcEdgeTypeEnum.TRUE_BRANCH));
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getType() == GfcEdgeTypeEnum.FALSE_BRANCH));
    }

    @Test
    void shouldRepresentDoWhileBodyBeforeDecision() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC do while",
                null,
                """
                        public class Exemplo {
                            void executar() {
                                do {
                                    trabalhar();
                                } while (ativo());
                            }
                        }
                        """
                ,
                null
        );

        Gfc graph = service.generate(input);

        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getCode().equals("N1") && node.getLabel().contains("trabalhar")));
        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getCode().equals("N2") && node.getType() == GfcNodeTypeEnum.LOOP));
        assertTrue(graph.getEdges().stream().anyMatch(edge ->
                edge.getSourceNodeCode().equals("N0")
                        && edge.getTargetNodeCode().equals("N1")
                        && edge.getType() == GfcEdgeTypeEnum.SEQUENTIAL));
        assertTrue(graph.getEdges().stream().anyMatch(edge ->
                edge.getSourceNodeCode().equals("N1")
                        && edge.getTargetNodeCode().equals("N2")
                        && edge.getType() == GfcEdgeTypeEnum.LOOP_BACK));
        assertTrue(graph.getEdges().stream().anyMatch(edge ->
                edge.getSourceNodeCode().equals("N2")
                        && edge.getTargetNodeCode().equals("N1")
                        && edge.getType() == GfcEdgeTypeEnum.LOOP_BODY));
        assertTrue(graph.getEdges().stream().anyMatch(edge ->
                edge.getSourceNodeCode().equals("N2")
                        && edge.getTargetNodeCode().equals("N_END")
                        && edge.getType() == GfcEdgeTypeEnum.LOOP_EXIT));
    }

    @Test
    void shouldGenerateBreakFlowToLoopExit() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC break",
                null,
                """
                        public class Exemplo {
                            void executar() {
                                while (ativo()) {
                                    if (erro()) {
                                        break;
                                    }
                                    trabalhar();
                                }
                                finalizar();
                            }
                        }
                        """,
                null
        );

        Gfc graph = service.generate(input);

        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getType() == GfcNodeTypeEnum.BREAK));
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getType() == GfcEdgeTypeEnum.BREAK_FLOW
                && graph.findNode(edge.getTargetNodeCode()).orElseThrow().getLabel().contains("finalizar")));
        assertTrue(graph.getEdges().stream().noneMatch(edge -> edge.getType() == GfcEdgeTypeEnum.LOOP_BACK
                && graph.findNode(edge.getSourceNodeCode()).orElseThrow().getType() == GfcNodeTypeEnum.BREAK));
    }

    @Test
    void shouldGenerateContinueFlowBackToLoop() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC continue",
                null,
                """
                        public class Exemplo {
                            void executar() {
                                while (ativo()) {
                                    if (ignorar()) {
                                        continue;
                                    }
                                    trabalhar();
                                }
                            }
                        }
                        """,
                null
        );

        Gfc graph = service.generate(input);

        String loopNodeCode = graph.getNodes().stream()
                .filter(node -> node.getType() == GfcNodeTypeEnum.LOOP)
                .findFirst()
                .orElseThrow()
                .getCode();
        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getType() == GfcNodeTypeEnum.CONTINUE));
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getType() == GfcEdgeTypeEnum.CONTINUE_FLOW
                && edge.getTargetNodeCode().equals(loopNodeCode)));
        assertTrue(graph.getEdges().stream().noneMatch(edge -> edge.getType() == GfcEdgeTypeEnum.LOOP_BACK
                && graph.findNode(edge.getSourceNodeCode()).orElseThrow().getType() == GfcNodeTypeEnum.CONTINUE));
    }

    @Test
    void shouldApplyBreakAndContinueToInnermostLoop() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC nested",
                null,
                """
                        public class Exemplo {
                            void executar() {
                                while (externo()) {
                                    for (int i = 0; i < 10; i++) {
                                        if (ignorar(i)) {
                                            continue;
                                        }
                                        if (parar(i)) {
                                            break;
                                        }
                                        trabalhar(i);
                                    }
                                    depoisInterno();
                                }
                            }
                        }
                        """,
                null
        );

        Gfc graph = service.generate(input);

        String innerLoopCode = graph.getNodes().stream()
                .filter(node -> node.getType() == GfcNodeTypeEnum.LOOP)
                .filter(node -> node.getLabel().contains("for (int i = 0; i < 10; i++)"))
                .findFirst()
                .orElseThrow()
                .getCode();
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getType() == GfcEdgeTypeEnum.CONTINUE_FLOW
                && edge.getTargetNodeCode().equals(innerLoopCode)));
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getType() == GfcEdgeTypeEnum.BREAK_FLOW
                && graph.findNode(edge.getTargetNodeCode()).orElseThrow().getLabel().contains("depoisInterno")));
    }

    @Test
    void shouldRouteNestedLoopBreakToStatementAfterInnerLoop() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC nested break",
                null,
                """
                        public class Exemplo {
                            void executar() {
                                for (int i = 0; i < 3; i++) {
                                    while (true) {
                                        break;
                                    }
                                    executarFor();
                                }
                            }
                        }
                        """,
                null
        );

        Gfc graph = service.generate(input);

        String outerLoopCode = graph.getNodes().stream()
                .filter(node -> node.getType() == GfcNodeTypeEnum.LOOP)
                .filter(node -> node.getLabel().contains("for (int i = 0; i < 3; i++)"))
                .findFirst()
                .orElseThrow()
                .getCode();
        String breakNodeCode = graph.getNodes().stream()
                .filter(node -> node.getType() == GfcNodeTypeEnum.BREAK)
                .findFirst()
                .orElseThrow()
                .getCode();

        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getSourceNodeCode().equals(breakNodeCode)
                && edge.getType() == GfcEdgeTypeEnum.BREAK_FLOW
                && graph.findNode(edge.getTargetNodeCode()).orElseThrow().getLabel().contains("executarFor")));
        assertTrue(graph.getEdges().stream().noneMatch(edge -> edge.getSourceNodeCode().equals(breakNodeCode)
                && edge.getTargetNodeCode().equals(outerLoopCode)));
        assertTrue(graph.getEdges().stream().noneMatch(edge -> edge.getSourceNodeCode().equals(breakNodeCode)
                && edge.getType() == GfcEdgeTypeEnum.LOOP_BACK));
    }

    @Test
    void shouldKeepNestedLoopContinueTargetingInnerLoop() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC nested continue",
                null,
                """
                        public class Exemplo {
                            void executar() {
                                for (int i = 0; i < 3; i++) {
                                    while (true) {
                                        continue;
                                    }
                                }
                            }
                        }
                        """,
                null
        );

        Gfc graph = service.generate(input);

        String innerLoopCode = graph.getNodes().stream()
                .filter(node -> node.getType() == GfcNodeTypeEnum.LOOP)
                .filter(node -> node.getLabel().contains("while (true)"))
                .findFirst()
                .orElseThrow()
                .getCode();
        String continueNodeCode = graph.getNodes().stream()
                .filter(node -> node.getType() == GfcNodeTypeEnum.CONTINUE)
                .findFirst()
                .orElseThrow()
                .getCode();

        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getSourceNodeCode().equals(continueNodeCode)
                && edge.getTargetNodeCode().equals(innerLoopCode)
                && edge.getType() == GfcEdgeTypeEnum.CONTINUE_FLOW));
    }

    @Test
    void shouldGenerateSwitchCasesDefaultAndBreakFlow() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC switch",
                null,
                """
                        public class Exemplo {
                            void executar(int tipo) {
                                switch (tipo) {
                                    case 1:
                                        executarA();
                                        break;
                                    case 2:
                                        executarB();
                                        break;
                                    default:
                                        executarPadrao();
                                }
                                proximo();
                            }
                        }
                        """,
                null
        );

        Gfc graph = service.generate(input);

        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getType() == GfcNodeTypeEnum.SWITCH
                && node.getLabel().contains("switch (tipo)")));
        assertEquals(0, graph.getNodes().stream().filter(node -> node.getType() == GfcNodeTypeEnum.CASE).count());
        assertEquals(3, graph.getNodes().stream().filter(node -> node.getType() == GfcNodeTypeEnum.CASE_BLOCK).count());
        assertEquals(2, graph.getEdges().stream().filter(edge -> edge.getType() == GfcEdgeTypeEnum.CASE_BRANCH).count());
        assertEquals(1, graph.getEdges().stream().filter(edge -> edge.getType() == GfcEdgeTypeEnum.DEFAULT_BRANCH).count());
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getType() == GfcEdgeTypeEnum.CASE_BRANCH
                && edge.getLabel().contains("case 1")
                && graph.findNode(edge.getTargetNodeCode()).orElseThrow().getLabel().contains("executarA()")));
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getType() == GfcEdgeTypeEnum.DEFAULT_BRANCH
                && edge.getLabel().contains("default")
                && graph.findNode(edge.getTargetNodeCode()).orElseThrow().getLabel().contains("executarPadrao()")));
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getType() == GfcEdgeTypeEnum.BREAK_FLOW
                && graph.findNode(edge.getTargetNodeCode()).orElseThrow().getLabel().contains("proximo")));
    }

    @Test
    void shouldGenerateSwitchExitWhenDefaultDoesNotExist() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC switch sem default",
                null,
                """
                        public class Exemplo {
                            void executar(int tipo) {
                                switch (tipo) {
                                    case 1:
                                        executarA();
                                        break;
                                }
                                proximo();
                            }
                        }
                        """,
                null
        );

        Gfc graph = service.generate(input);

        String switchCode = graph.getNodes().stream()
                .filter(node -> node.getType() == GfcNodeTypeEnum.SWITCH)
                .findFirst()
                .orElseThrow()
                .getCode();
        assertEquals(0, graph.getNodes().stream().filter(node -> node.getType() == GfcNodeTypeEnum.CASE).count());
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getSourceNodeCode().equals(switchCode)
                && edge.getType() == GfcEdgeTypeEnum.CASE_BRANCH
                && edge.getLabel().contains("case 1")
                && graph.findNode(edge.getTargetNodeCode()).orElseThrow().getType() == GfcNodeTypeEnum.CASE_BLOCK));
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getSourceNodeCode().equals(switchCode)
                && edge.getType() == GfcEdgeTypeEnum.SEQUENTIAL
                && graph.findNode(edge.getTargetNodeCode()).orElseThrow().getLabel().contains("proximo")));
    }

    @Test
    void shouldGenerateSwitchFallThroughToNextCase() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC fall through",
                null,
                """
                        public class Exemplo {
                            void executar(int tipo) {
                                switch (tipo) {
                                    case 1:
                                        executarA();
                                    case 2:
                                        executarB();
                                        break;
                                }
                                proximo();
                            }
                        }
                        """,
                null
        );

        Gfc graph = service.generate(input);

        String caseOneBlockCode = graph.getNodes().stream()
                .filter(node -> node.getType() == GfcNodeTypeEnum.CASE_BLOCK)
                .filter(node -> node.getLabel().contains("executarA"))
                .findFirst()
                .orElseThrow()
                .getCode();
        String caseTwoBlockCode = graph.getNodes().stream()
                .filter(node -> node.getType() == GfcNodeTypeEnum.CASE_BLOCK)
                .filter(node -> node.getLabel().contains("executarB"))
                .findFirst()
                .orElseThrow()
                .getCode();
        assertEquals(0, graph.getNodes().stream().filter(node -> node.getType() == GfcNodeTypeEnum.CASE).count());
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getType() == GfcEdgeTypeEnum.CASE_BRANCH
                && edge.getLabel().contains("case 1")
                && edge.getTargetNodeCode().equals(caseOneBlockCode)));
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getType() == GfcEdgeTypeEnum.CASE_BRANCH
                && edge.getLabel().contains("case 2")
                && edge.getTargetNodeCode().equals(caseTwoBlockCode)));
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getSourceNodeCode().equals(caseOneBlockCode)
                && edge.getTargetNodeCode().equals(caseTwoBlockCode)
                && edge.getType() == GfcEdgeTypeEnum.SEQUENTIAL));
        assertTrue(graph.getEdges().stream().noneMatch(edge -> edge.getSourceNodeCode().equals(caseOneBlockCode)
                && graph.findNode(edge.getTargetNodeCode()).orElseThrow().getLabel().contains("proximo")));
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getSourceNodeCode().equals(caseTwoBlockCode)
                && graph.findNode(edge.getTargetNodeCode()).orElseThrow().getLabel().contains("proximo")));
    }

    @Test
    void shouldRouteGroupedCasesToSameCaseBlock() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC grouped cases",
                null,
                """
                        public class Exemplo {
                            void executar(int tipo) {
                                switch (tipo) {
                                    case 3:
                                    case 4:
                                        System.out.println("Numero tres ou quatro");
                                        break;
                                }
                                proximo();
                            }
                        }
                        """,
                null
        );

        Gfc graph = service.generate(input);

        String caseThreeTargetCode = graph.getEdges().stream()
                .filter(edge -> edge.getType() == GfcEdgeTypeEnum.CASE_BRANCH)
                .filter(edge -> edge.getLabel().equals("case 3"))
                .findFirst()
                .orElseThrow()
                .getTargetNodeCode();
        String caseFourTargetCode = graph.getEdges().stream()
                .filter(edge -> edge.getType() == GfcEdgeTypeEnum.CASE_BRANCH)
                .filter(edge -> edge.getLabel().equals("case 4"))
                .findFirst()
                .orElseThrow()
                .getTargetNodeCode();

        assertEquals(1, graph.getNodes().stream().filter(node -> node.getType() == GfcNodeTypeEnum.CASE_BLOCK).count());
        assertEquals(0, graph.getNodes().stream().filter(node -> node.getType() == GfcNodeTypeEnum.CASE).count());
        assertEquals(caseThreeTargetCode, caseFourTargetCode);
        assertEquals(GfcNodeTypeEnum.CASE_BLOCK, graph.findNode(caseThreeTargetCode).orElseThrow().getType());
        assertTrue(graph.findNode(caseThreeTargetCode).orElseThrow().getLabel().contains("Numero tres ou quatro"));
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getSourceNodeCode().equals(caseThreeTargetCode)
                && graph.findNode(edge.getTargetNodeCode()).orElseThrow().getLabel().contains("proximo")));
    }

    @Test
    void shouldRouteSwitchBreakInsideLoopToStatementAfterSwitch() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC switch in loop",
                null,
                """
                        public class Exemplo {
                            void executar(int tipo) {
                                while (ativo()) {
                                    switch (tipo) {
                                        case 1:
                                            break;
                                    }
                                    depoisSwitch();
                                }
                            }
                        }
                        """,
                null
        );

        Gfc graph = service.generate(input);

        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getType() == GfcEdgeTypeEnum.BREAK_FLOW
                && graph.findNode(edge.getTargetNodeCode()).orElseThrow().getLabel().contains("depoisSwitch")));
        assertTrue(graph.getEdges().stream().noneMatch(edge -> edge.getType() == GfcEdgeTypeEnum.LOOP_BACK
                && graph.findNode(edge.getSourceNodeCode()).orElseThrow().getType() == GfcNodeTypeEnum.BREAK));
    }

    @Test
    void shouldRespectLoopBreakInsideSwitchAndSwitchBreakAfterLoop() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC loop in switch",
                null,
                """
                        public class Exemplo {
                            void executar(int tipo) {
                                switch (tipo) {
                                    case 1:
                                        while (ativo()) {
                                            break;
                                        }
                                        depoisWhile();
                                        break;
                                }
                                proximo();
                            }
                        }
                        """,
                null
        );

        Gfc graph = service.generate(input);

        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getType() == GfcEdgeTypeEnum.BREAK_FLOW
                && graph.findNode(edge.getTargetNodeCode()).orElseThrow().getLabel().contains("depoisWhile")));
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getType() == GfcEdgeTypeEnum.BREAK_FLOW
                && graph.findNode(edge.getTargetNodeCode()).orElseThrow().getLabel().contains("proximo")));
    }

    @Test
    void shouldGenerateThrowFlowToEnd() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC throw",
                null,
                """
                        public class Exemplo {
                            void executar() {
                                throw new IllegalArgumentException();
                            }
                        }
                        """,
                null
        );

        Gfc graph = service.generate(input);

        String throwCode = graph.getNodes().stream()
                .filter(node -> node.getType() == GfcNodeTypeEnum.THROW)
                .findFirst()
                .orElseThrow()
                .getCode();
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getSourceNodeCode().equals(throwCode)
                && edge.getTargetNodeCode().equals("N_END")
                && edge.getType() == GfcEdgeTypeEnum.THROW_FLOW));
    }

    @Test
    void shouldGenerateThrowFlowInsideIfAndKeepFalseBranchFlow() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC throw if",
                null,
                """
                        public class Exemplo {
                            int executar(int valor) {
                                if (valor < 0) {
                                    throw new IllegalArgumentException();
                                }
                                return valor;
                            }
                        }
                        """,
                null
        );

        Gfc graph = service.generate(input);

        String throwCode = graph.getNodes().stream()
                .filter(node -> node.getType() == GfcNodeTypeEnum.THROW)
                .findFirst()
                .orElseThrow()
                .getCode();
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getTargetNodeCode().equals(throwCode)
                && edge.getType() == GfcEdgeTypeEnum.TRUE_BRANCH));
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getSourceNodeCode().equals(throwCode)
                && edge.getTargetNodeCode().equals("N_END")
                && edge.getType() == GfcEdgeTypeEnum.THROW_FLOW));
        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getType() == GfcNodeTypeEnum.RETURN
                && node.getLabel().contains("return valor")));
    }

    @Test
    void shouldGenerateTryCatchFlow() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC try catch",
                null,
                """
                        public class Exemplo {
                            void executar() {
                                try {
                                    executarAcao();
                                } catch (Exception e) {
                                    tratarErro();
                                }
                                proximo();
                            }
                        }
                        """,
                null
        );

        Gfc graph = service.generate(input);

        String executarCode = graph.getNodes().stream()
                .filter(node -> node.getLabel().contains("executarAcao"))
                .findFirst()
                .orElseThrow()
                .getCode();
        String tratarCode = graph.getNodes().stream()
                .filter(node -> node.getLabel().contains("tratarErro"))
                .findFirst()
                .orElseThrow()
                .getCode();
        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getType() == GfcNodeTypeEnum.TRY));
        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getType() == GfcNodeTypeEnum.CATCH
                && node.getLabel().contains("Exception e")));
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getType() == GfcEdgeTypeEnum.TRY_BRANCH));
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getType() == GfcEdgeTypeEnum.CATCH_BRANCH));
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getSourceNodeCode().equals(executarCode)
                && graph.findNode(edge.getTargetNodeCode()).orElseThrow().getLabel().contains("proximo")));
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getSourceNodeCode().equals(tratarCode)
                && graph.findNode(edge.getTargetNodeCode()).orElseThrow().getLabel().contains("proximo")));
    }

    @Test
    void shouldGenerateTryCatchFinallyFlowThroughFinally() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC try finally",
                null,
                """
                        public class Exemplo {
                            void executar() {
                                try {
                                    executarAcao();
                                } catch (Exception e) {
                                    tratarErro();
                                } finally {
                                    finalizar();
                                }
                                proximo();
                            }
                        }
                        """,
                null
        );

        Gfc graph = service.generate(input);

        String finallyCode = graph.getNodes().stream()
                .filter(node -> node.getType() == GfcNodeTypeEnum.FINALLY)
                .findFirst()
                .orElseThrow()
                .getCode();
        String finalizarCode = graph.getNodes().stream()
                .filter(node -> node.getLabel().contains("finalizar"))
                .findFirst()
                .orElseThrow()
                .getCode();
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getTargetNodeCode().equals(finallyCode)
                && edge.getType() == GfcEdgeTypeEnum.FINALLY_BRANCH));
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getSourceNodeCode().equals(finalizarCode)
                && graph.findNode(edge.getTargetNodeCode()).orElseThrow().getLabel().contains("proximo")));
    }

    @Test
    void shouldGenerateTryFinallyWithoutCatch() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC try finally sem catch",
                null,
                """
                        public class Exemplo {
                            void executar() {
                                try {
                                    executarAcao();
                                } finally {
                                    finalizar();
                                }
                                proximo();
                            }
                        }
                        """,
                null
        );

        Gfc graph = service.generate(input);

        String finalizarCode = graph.getNodes().stream()
                .filter(node -> node.getLabel().contains("finalizar"))
                .findFirst()
                .orElseThrow()
                .getCode();
        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getType() == GfcNodeTypeEnum.TRY));
        assertEquals(1, graph.getNodes().stream().filter(node -> node.getType() == GfcNodeTypeEnum.FINALLY).count());
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getType() == GfcEdgeTypeEnum.FINALLY_BRANCH));
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getSourceNodeCode().equals(finalizarCode)
                && graph.findNode(edge.getTargetNodeCode()).orElseThrow().getLabel().contains("proximo")));
    }

    @Test
    void shouldRouteThrowInsideTryToFirstCatch() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC throw catch",
                null,
                """
                        public class Exemplo {
                            void executar() {
                                try {
                                    throw new RuntimeException();
                                } catch (Exception e) {
                                    tratarErro();
                                }
                                proximo();
                            }
                        }
                        """,
                null
        );

        Gfc graph = service.generate(input);

        String catchCode = graph.getNodes().stream()
                .filter(node -> node.getType() == GfcNodeTypeEnum.CATCH)
                .findFirst()
                .orElseThrow()
                .getCode();
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getType() == GfcEdgeTypeEnum.THROW_FLOW
                && edge.getTargetNodeCode().equals(catchCode)));
    }

    @Test
    void shouldRouteThrowInsideTryToFinallyWhenCatchDoesNotExist() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC throw finally",
                null,
                """
                        public class Exemplo {
                            void executar() {
                                try {
                                    throw new RuntimeException();
                                } finally {
                                    finalizar();
                                }
                                proximo();
                            }
                        }
                        """,
                null
        );

        Gfc graph = service.generate(input);

        String throwCode = graph.getNodes().stream()
                .filter(node -> node.getType() == GfcNodeTypeEnum.THROW)
                .findFirst()
                .orElseThrow()
                .getCode();
        String finallyCode = graph.getEdges().stream()
                .filter(edge -> edge.getSourceNodeCode().equals(throwCode))
                .filter(edge -> edge.getType() == GfcEdgeTypeEnum.THROW_FLOW)
                .findFirst()
                .orElseThrow()
                .getTargetNodeCode();
        String finalizarCode = graph.getEdges().stream()
                .filter(edge -> edge.getSourceNodeCode().equals(finallyCode))
                .findFirst()
                .orElseThrow()
                .getTargetNodeCode();
        assertEquals(GfcNodeTypeEnum.FINALLY, graph.findNode(finallyCode).orElseThrow().getType());
        assertEquals(1, graph.getNodes().stream().filter(node -> node.getType() == GfcNodeTypeEnum.FINALLY).count());
        assertTrue(graph.findNode(finalizarCode).orElseThrow().getLabel().contains("finalizar"));
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getSourceNodeCode().equals(finalizarCode)
                && edge.getTargetNodeCode().equals("N_END")));
        assertTrue(graph.getEdges().stream().noneMatch(edge -> edge.getSourceNodeCode().equals(finalizarCode)
                && graph.findNode(edge.getTargetNodeCode()).orElseThrow().getLabel().contains("proximo")));
    }

    @Test
    void shouldGenerateMultipleCatchBranches() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC multi catch",
                null,
                """
                        public class Exemplo {
                            void executar() {
                                try {
                                    executarAcao();
                                } catch (IllegalArgumentException e) {
                                    tratarIllegal();
                                } catch (Exception e) {
                                    tratarException();
                                }
                                proximo();
                            }
                        }
                        """,
                null
        );

        Gfc graph = service.generate(input);

        assertEquals(2, graph.getNodes().stream().filter(node -> node.getType() == GfcNodeTypeEnum.CATCH).count());
        assertEquals(2, graph.getEdges().stream().filter(edge -> edge.getType() == GfcEdgeTypeEnum.CATCH_BRANCH).count());
    }

    @Test
    void shouldRouteThrowInsideCatchToEnd() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC throw catch body",
                null,
                """
                        public class Exemplo {
                            void executar() {
                                try {
                                    executarAcao();
                                } catch (Exception e) {
                                    throw new RuntimeException();
                                }
                                proximo();
                            }
                        }
                        """,
                null
        );

        Gfc graph = service.generate(input);

        String throwCode = graph.getNodes().stream()
                .filter(node -> node.getType() == GfcNodeTypeEnum.THROW)
                .findFirst()
                .orElseThrow()
                .getCode();
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getSourceNodeCode().equals(throwCode)
                && edge.getTargetNodeCode().equals("N_END")
                && edge.getType() == GfcEdgeTypeEnum.THROW_FLOW));
    }

    @Test
    void shouldRouteThrowInsideCatchToFinallyAndEndWithoutFollowingNextStatement() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC throw catch finally",
                null,
                """
                        public class Exemplo {
                            void executar() {
                                try {
                                    executarAcao();
                                } catch (Exception e) {
                                    throw new RuntimeException();
                                } finally {
                                    finalizar();
                                }
                                proximo();
                            }
                        }
                        """,
                null
        );

        Gfc graph = service.generate(input);

        String throwCode = graph.getNodes().stream()
                .filter(node -> node.getType() == GfcNodeTypeEnum.THROW)
                .findFirst()
                .orElseThrow()
                .getCode();
        String finallyCode = graph.getEdges().stream()
                .filter(edge -> edge.getSourceNodeCode().equals(throwCode))
                .filter(edge -> edge.getType() == GfcEdgeTypeEnum.THROW_FLOW)
                .findFirst()
                .orElseThrow()
                .getTargetNodeCode();
        List<String> finalizarCodes = graph.getEdges().stream()
                .filter(edge -> edge.getSourceNodeCode().equals(finallyCode))
                .map(edge -> edge.getTargetNodeCode())
                .filter(targetNodeCode -> graph.findNode(targetNodeCode).orElseThrow().getLabel().contains("finalizar"))
                .toList();

        assertEquals(GfcNodeTypeEnum.FINALLY, graph.findNode(finallyCode).orElseThrow().getType());
        assertEquals(1, graph.getNodes().stream().filter(node -> node.getType() == GfcNodeTypeEnum.FINALLY).count());
        assertTrue(finalizarCodes.stream().anyMatch(finalizarCode -> graph.getEdges().stream().anyMatch(edge -> edge.getSourceNodeCode().equals(finalizarCode)
                && edge.getTargetNodeCode().equals("N_END"))));
    }

    @Test
    void shouldKeepTryCatchFlowInsideLoopReturningToLoop() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC try loop",
                null,
                """
                        public class Exemplo {
                            void executar() {
                                while (ativo()) {
                                    try {
                                        executarAcao();
                                    } catch (Exception e) {
                                        tratar();
                                    }
                                    continuar();
                                }
                            }
                        }
                        """,
                null
        );

        Gfc graph = service.generate(input);

        String continuarCode = graph.getNodes().stream()
                .filter(node -> node.getLabel().contains("continuar"))
                .findFirst()
                .orElseThrow()
                .getCode();
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getSourceNodeCode().equals(continuarCode)
                && edge.getType() == GfcEdgeTypeEnum.LOOP_BACK));
    }

    @Test
    void shouldKeepSwitchBreakWorkingInsideTry() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC switch try",
                null,
                """
                        public class Exemplo {
                            void executar(int tipo) {
                                try {
                                    switch (tipo) {
                                        case 1:
                                            executarAcao();
                                            break;
                                    }
                                } catch (Exception e) {
                                    tratar();
                                }
                                proximo();
                            }
                        }
                        """,
                null
        );

        Gfc graph = service.generate(input);

        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getType() == GfcNodeTypeEnum.SWITCH));
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getType() == GfcEdgeTypeEnum.BREAK_FLOW
                && graph.findNode(edge.getTargetNodeCode()).orElseThrow().getLabel().contains("proximo")));
        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getType() == GfcNodeTypeEnum.CATCH));
    }

    @Test
    void shouldGenerateTernaryFlowForVariableDeclaration() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC ternario declaracao",
                null,
                """
                        public class Exemplo {
                            void executar(int valor) {
                                int resultado = valor > 0 ? 1 : -1;
                                proximo();
                            }
                        }
                        """,
                null
        );

        Gfc graph = service.generate(input);

        String trueBranchCode = graph.getNodes().stream()
                .filter(node -> node.getLabel().contains("int resultado = 1"))
                .findFirst()
                .orElseThrow()
                .getCode();
        String falseBranchCode = graph.getNodes().stream()
                .filter(node -> node.getLabel().contains("int resultado = -1"))
                .findFirst()
                .orElseThrow()
                .getCode();

        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getType() == GfcNodeTypeEnum.TERNARY
                && node.getLabel().contains("valor > 0")));
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getType() == GfcEdgeTypeEnum.TRUE_BRANCH
                && edge.getTargetNodeCode().equals(trueBranchCode)));
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getType() == GfcEdgeTypeEnum.FALSE_BRANCH
                && edge.getTargetNodeCode().equals(falseBranchCode)));
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getSourceNodeCode().equals(trueBranchCode)
                && graph.findNode(edge.getTargetNodeCode()).orElseThrow().getLabel().contains("proximo")));
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getSourceNodeCode().equals(falseBranchCode)
                && graph.findNode(edge.getTargetNodeCode()).orElseThrow().getLabel().contains("proximo")));
    }

    @Test
    void shouldGenerateTernaryFlowForAssignment() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC ternario atribuicao",
                null,
                """
                        public class Exemplo {
                            void executar(int valor) {
                                resultado = valor > 0 ? 1 : -1;
                                proximo();
                            }
                        }
                        """,
                null
        );

        Gfc graph = service.generate(input);

        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getType() == GfcNodeTypeEnum.TERNARY));
        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getLabel().contains("resultado = 1")));
        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getLabel().contains("resultado = -1")));
    }

    @Test
    void shouldGenerateTernaryFlowForReturn() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC ternario return",
                null,
                """
                        public class Exemplo {
                            int executar(int valor) {
                                return valor > 0 ? 1 : -1;
                            }
                        }
                        """,
                null
        );

        Gfc graph = service.generate(input);

        assertEquals(1, graph.getNodes().stream().filter(node -> node.getType() == GfcNodeTypeEnum.TERNARY).count());
        assertEquals(2, graph.getNodes().stream().filter(node -> node.getType() == GfcNodeTypeEnum.RETURN).count());
        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getLabel().contains("return 1")));
        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getLabel().contains("return -1")));
        assertTrue(graph.getNodes().stream()
                .filter(node -> node.getType() == GfcNodeTypeEnum.RETURN)
                .allMatch(returnNode -> graph.getEdges().stream().anyMatch(edge -> edge.getSourceNodeCode().equals(returnNode.getCode())
                        && edge.getTargetNodeCode().equals("N_END"))));
    }

    @Test
    void shouldGenerateTernaryFlowForMethodCallArgument() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC ternario argumento",
                null,
                """
                        public class Exemplo {
                            void executar(int valor) {
                                imprimir(valor > 0 ? "positivo" : "negativo");
                                proximo();
                            }
                        }
                        """,
                null
        );

        Gfc graph = service.generate(input);

        String positiveCode = graph.getNodes().stream()
                .filter(node -> node.getLabel().contains("imprimir(\"positivo\")"))
                .findFirst()
                .orElseThrow()
                .getCode();
        String negativeCode = graph.getNodes().stream()
                .filter(node -> node.getLabel().contains("imprimir(\"negativo\")"))
                .findFirst()
                .orElseThrow()
                .getCode();

        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getType() == GfcNodeTypeEnum.TERNARY));
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getSourceNodeCode().equals(positiveCode)
                && graph.findNode(edge.getTargetNodeCode()).orElseThrow().getLabel().contains("proximo")));
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getSourceNodeCode().equals(negativeCode)
                && graph.findNode(edge.getTargetNodeCode()).orElseThrow().getLabel().contains("proximo")));
    }

    @Test
    void shouldReplaceOnlyProcessedTernaryOccurrenceInStatementLabel() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC ternario repetido",
                null,
                """
                        public class Exemplo {
                            void executar(int valor) {
                                combinar(valor > 0 ? 1 : -1, valor > 0 ? 1 : -1);
                            }
                        }
                        """,
                null
        );

        Gfc graph = service.generate(input);

        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getLabel().contains("combinar(1, valor > 0 ? 1 : -1)")));
        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getLabel().contains("combinar(-1, valor > 0 ? 1 : -1)")));
        assertTrue(graph.getNodes().stream().noneMatch(node -> node.getLabel().contains("combinar(1, 1)")));
        assertTrue(graph.getNodes().stream().noneMatch(node -> node.getLabel().contains("combinar(-1, -1)")));
    }

    @Test
    void shouldGenerateNestedTernaryFlow() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC ternario aninhado",
                null,
                """
                        public class Exemplo {
                            void executar(int valor) {
                                int resultado = valor > 0 ? 1 : valor == 0 ? 0 : -1;
                                proximo();
                            }
                        }
                        """,
                null
        );

        Gfc graph = service.generate(input);

        String firstTernaryCode = graph.getNodes().stream()
                .filter(node -> node.getType() == GfcNodeTypeEnum.TERNARY)
                .filter(node -> node.getLabel().contains("valor > 0"))
                .findFirst()
                .orElseThrow()
                .getCode();
        String secondTernaryCode = graph.getNodes().stream()
                .filter(node -> node.getType() == GfcNodeTypeEnum.TERNARY)
                .filter(node -> node.getLabel().contains("valor == 0"))
                .findFirst()
                .orElseThrow()
                .getCode();

        assertEquals(2, graph.getNodes().stream().filter(node -> node.getType() == GfcNodeTypeEnum.TERNARY).count());
        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getSourceNodeCode().equals(firstTernaryCode)
                && edge.getTargetNodeCode().equals(secondTernaryCode)
                && edge.getType() == GfcEdgeTypeEnum.FALSE_BRANCH));
        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getLabel().contains("int resultado = 1")));
        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getLabel().contains("int resultado = 0")));
        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getLabel().contains("int resultado = -1")));
    }

    @Test
    void shouldRemoveLineCommentBeforeTernaryDeclarationLabels() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC comentarios",
                null,
                """
                        public class Exemplo {
                            void executar(int valor) {
                                // comentario antes
                                int resultado = valor > 0 ? 1 : -1;
                            }
                        }
                        """,
                null
        );

        Gfc graph = service.generate(input);

        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getLabel().contains("int resultado = 1")));
        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getLabel().contains("int resultado = -1")));
        assertTrue(graph.getNodes().stream().noneMatch(node -> node.getLabel().contains("comentario antes")));
    }

    @Test
    void shouldRemoveTrailingCommentFromStatementLabels() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC comentario lateral",
                null,
                """
                        public class Exemplo {
                            void executar(int valor) {
                                int resultado = valor > 0 ? 1 : -1; // comentario lateral
                            }
                        }
                        """,
                null
        );

        Gfc graph = service.generate(input);

        assertTrue(graph.getNodes().stream().noneMatch(node -> node.getLabel().contains("comentario lateral")));
        assertTrue(graph.getNodes().stream().noneMatch(node -> node.getLabel().contains("//")));
    }

    @Test
    void shouldRemoveBlockCommentFromStatementLabels() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC comentario bloco",
                null,
                """
                        public class Exemplo {
                            void executar() {
                                /* comentario de bloco */
                                executarAcao();
                            }
                        }
                        """,
                null
        );

        Gfc graph = service.generate(input);

        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getLabel().contains("executarAcao();")));
        assertTrue(graph.getNodes().stream().noneMatch(node -> node.getLabel().contains("comentario de bloco")));
        assertTrue(graph.getNodes().stream().noneMatch(node -> node.getLabel().contains("/*")));
        assertTrue(graph.getNodes().stream().noneMatch(node -> node.getLabel().contains("*/")));
    }

    @Test
    void shouldKeepCommentLikeTextInsideStringLiterals() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC strings",
                null,
                """
                        public class Exemplo {
                            void executar() {
                                System.out.println("http://localhost:8080");
                                System.out.println("// texto literal");
                            }
                        }
                        """,
                null
        );

        Gfc graph = service.generate(input);

        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getLabel().contains("\"http://localhost:8080\"")));
        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getLabel().contains("\"// texto literal\"")));
    }

    @Test
    void shouldRejectInvalidJavaSourceCode() {
        PreviewGfcInput input = new PreviewGfcInput(UUID.randomUUID(), "GFC", null, "class {", null);

        InvalidJavaSourceCodeException exception = assertThrows(InvalidJavaSourceCodeException.class, () -> service.generate(input));

        assertTrue(exception.getMessage().contains("invalido"));
    }

    @Test
    void shouldRejectSourceWithoutMethodOrValidSnippet() {
        PreviewGfcInput input = new PreviewGfcInput(UUID.randomUUID(), "GFC", null, "package exemplo;", null);

        GfcMethodNotFoundException exception = assertThrows(GfcMethodNotFoundException.class, () -> service.generate(input));

        assertTrue(exception.getMessage().contains("Nenhum metodo ou trecho Java valido"));
    }

    @Test
    void shouldGeneratePreviewForSelectedMethodSignature() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC selecionado",
                null,
                """
                        public class Exemplo {
                            int soma(int a, int b) {
                                return a + b;
                            }

                            int subtrai(int a, int b) {
                                int resultado = a - b;
                                return resultado;
                            }
                        }
                        """,
                "int subtrai(int a, int b)"
        );

        Gfc graph = service.generate(input);

        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getLabel().contains("int resultado = a - b")));
        assertTrue(graph.getNodes().stream().noneMatch(node -> node.getLabel().contains("return a + b")));
    }

    @Test
    void shouldRejectUnknownMethodSignature() {
        PreviewGfcInput input = new PreviewGfcInput(
                UUID.randomUUID(),
                "GFC",
                null,
                "class Exemplo { int soma(int a, int b) { return a + b; } }",
                "int inexistente()"
        );

        GfcMethodNotFoundException exception = assertThrows(GfcMethodNotFoundException.class, () -> service.generate(input));

        assertTrue(exception.getMessage().contains("Metodo informado nao foi encontrado"));
    }
}
