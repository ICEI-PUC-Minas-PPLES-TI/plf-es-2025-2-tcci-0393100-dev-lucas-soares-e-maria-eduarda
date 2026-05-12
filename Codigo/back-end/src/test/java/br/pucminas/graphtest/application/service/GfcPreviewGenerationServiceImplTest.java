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

        assertTrue(graph.getEdges().stream().anyMatch(edge -> edge.getType() == GfcEdgeTypeEnum.LOOP_BACK));
    }

    @Test
    void shouldKeepSimplifiedSupportForForAndForEachLoops() {
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

        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getLabel().contains("for (i < 10)")));
        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getLabel().contains("for (String item : itens)")));
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
        assertTrue(graph.getNodes().stream().anyMatch(node -> node.getCode().equals("N2") && node.getLabel().contains("do while")));
        assertTrue(graph.getEdges().stream().anyMatch(edge ->
                edge.getSourceNodeCode().equals("N0")
                        && edge.getTargetNodeCode().equals("N1")
                        && edge.getType() == GfcEdgeTypeEnum.SEQUENTIAL));
        assertTrue(graph.getEdges().stream().anyMatch(edge ->
                edge.getSourceNodeCode().equals("N1")
                        && edge.getTargetNodeCode().equals("N2")
                        && edge.getType() == GfcEdgeTypeEnum.SEQUENTIAL));
        assertTrue(graph.getEdges().stream().anyMatch(edge ->
                edge.getSourceNodeCode().equals("N2")
                        && edge.getTargetNodeCode().equals("N1")
                        && edge.getType() == GfcEdgeTypeEnum.TRUE_BRANCH));
        assertTrue(graph.getEdges().stream().anyMatch(edge ->
                edge.getSourceNodeCode().equals("N2")
                        && edge.getTargetNodeCode().equals("N_END")
                        && edge.getType() == GfcEdgeTypeEnum.FALSE_BRANCH));
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
