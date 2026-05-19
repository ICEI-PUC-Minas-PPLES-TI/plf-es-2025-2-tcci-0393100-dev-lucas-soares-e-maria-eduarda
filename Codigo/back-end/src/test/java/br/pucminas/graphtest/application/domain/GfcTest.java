package br.pucminas.graphtest.application.domain;

import br.pucminas.graphtest.application.domain.gfc.enums.GfcEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.gfc.enums.GfcNodeTypeEnum;
import br.pucminas.graphtest.application.domain.gfc.model.Gfc;
import br.pucminas.graphtest.application.domain.gfc.model.GfcEdge;
import br.pucminas.graphtest.application.domain.gfc.model.GfcNode;
import br.pucminas.graphtest.application.exception.InvalidGfcModelException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static br.pucminas.graphtest.application.domain.gfc.rules.GfcDomainRules.JAVA_LANGUAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GfcTest {

    private final UUID sourceFileId = UUID.randomUUID();

    @Test
    void shouldCreateControlFlowGraphWithNodesAndEdges() {
        GfcNode start = GfcNode.start(UUID.randomUUID(), "N1", "Inicio");
        GfcNode decision = GfcNode.decision(UUID.randomUUID(), "N2", "x > 0", 2, 2);
        GfcNode end = GfcNode.end(UUID.randomUUID(), "N3", "Fim");
        GfcEdge edgeOne = new GfcEdge(UUID.randomUUID(), "N1", "N2", GfcEdgeTypeEnum.SEQUENTIAL, null);
        GfcEdge edgeTwo = new GfcEdge(UUID.randomUUID(), "N2", "N3", GfcEdgeTypeEnum.TRUE_BRANCH, "true");

        Gfc gfc = new Gfc(
                UUID.randomUUID(),
                UUID.randomUUID(),
                sourceFileId,
                "void calcular()",
                "Metodo calcular",
                "Fluxo principal",
                "Java",
                List.of(start, decision, end),
                List.of(edgeOne, edgeTwo)
        );

        assertEquals("Metodo calcular", gfc.getName());
        assertEquals(JAVA_LANGUAGE, gfc.getLanguage());
        assertEquals(3, gfc.getNodes().size());
        assertEquals(2, gfc.getEdges().size());
        assertEquals(1, gfc.outgoingEdges("N1").size());
        assertNull(start.getStartLine());
        assertNull(end.getEndLine());
        assertTrue(gfc.findNode("N2").orElseThrow().isDecision());
    }

    @Test
    void shouldRejectNonJavaLanguage() {
        GfcNode start = GfcNode.start(UUID.randomUUID(), "N1", "Inicio");

        InvalidGfcModelException exception = assertThrows(
                InvalidGfcModelException.class,
                () -> new Gfc(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        sourceFileId,
                        "void metodo()",
                        "Metodo",
                        null,
                        "Kotlin",
                        List.of(start),
                        List.of()
                )
        );

        assertTrue(exception.getMessage().contains("Java"));
    }

    @Test
    void shouldRejectPersistedGraphWithoutSourceFileId() {
        GfcNode start = GfcNode.start(UUID.randomUUID(), "N1", "Inicio");

        InvalidGfcModelException exception = assertThrows(
                InvalidGfcModelException.class,
                () -> new Gfc(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        null,
                        "void metodo()",
                        "Metodo",
                        null,
                        "Java",
                        List.of(start),
                        List.of()
                )
        );

        assertTrue(exception.getMessage().contains("arquivo-fonte"));
    }

    @Test
    void shouldAllowPreviewGraphWithoutSourceFileId() {
        GfcNode start = GfcNode.start(UUID.randomUUID(), "N0", "Inicio");

        Gfc preview = Gfc.preview(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "void metodo()",
                "Metodo",
                null,
                "Java",
                List.of(start),
                List.of()
        );

        assertNull(preview.getSourceFileId());
        assertEquals("void metodo()", preview.getMethodSignature());
    }

    @Test
    void shouldRejectDuplicatedNodeCode() {
        GfcNode start = GfcNode.start(UUID.randomUUID(), "N1", "Inicio");
        GfcNode statement = GfcNode.statement(UUID.randomUUID(), "N1", "int x = 1", 2, 2);

        InvalidGfcModelException exception = assertThrows(
                InvalidGfcModelException.class,
                () -> new Gfc(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        sourceFileId,
                        "void metodo()",
                        "Metodo",
                        null,
                        "Java",
                        List.of(start, statement),
                        List.of()
                )
        );

        assertTrue(exception.getMessage().contains("duplicado"));
    }

    @Test
    void shouldRejectEdgeReferencingUnknownNode() {
        GfcNode start = GfcNode.start(UUID.randomUUID(), "N1", "Inicio");
        GfcEdge edge = new GfcEdge(UUID.randomUUID(), "N1", "N2", GfcEdgeTypeEnum.SEQUENTIAL, null);

        InvalidGfcModelException exception = assertThrows(
                InvalidGfcModelException.class,
                () -> new Gfc(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        sourceFileId,
                        "void metodo()",
                        "Metodo",
                        null,
                        "Java",
                        List.of(start),
                        List.of(edge)
                )
        );

        assertTrue(exception.getMessage().contains("N2"));
    }

    @Test
    void shouldRejectInvalidNodeLineRange() {
        InvalidGfcModelException exception = assertThrows(
                InvalidGfcModelException.class,
                () -> new GfcNode(UUID.randomUUID(), "N1", "Comando", GfcNodeTypeEnum.STATEMENT, 10, 9)
        );

        assertTrue(exception.getMessage().contains("linha final"));
    }

    @Test
    void shouldRejectMissingLineForSourceCodeNode() {
        InvalidGfcModelException exception = assertThrows(
                InvalidGfcModelException.class,
                () -> GfcNode.statement(UUID.randomUUID(), "N1", "int x = 1", null, 1)
        );

        assertTrue(exception.getMessage().contains("linha inicial"));
    }

    @Test
    void shouldAllowSelfLoopEdgeWhenNodeExists() {
        GfcNode decision = GfcNode.decision(UUID.randomUUID(), "N1", "while (ativo)", 3, 3);
        GfcEdge loop = new GfcEdge(UUID.randomUUID(), "N1", "N1", GfcEdgeTypeEnum.LOOP_BACK, "loop");

        Gfc gfc = new Gfc(
                UUID.randomUUID(),
                UUID.randomUUID(),
                sourceFileId,
                "void metodo()",
                "Metodo",
                null,
                "Java",
                List.of(decision),
                List.of(loop)
        );

        assertEquals(sourceFileId, gfc.getSourceFileId());
        assertEquals("void metodo()", gfc.getMethodSignature());
        assertEquals(1, gfc.outgoingEdges("N1").size());
        assertEquals(1, gfc.incomingEdges("N1").size());
    }

    @Test
    void shouldAcceptAdvancedControlFlowNodeTypes() {
        List<GfcNodeTypeEnum> advancedTypes = List.of(
                GfcNodeTypeEnum.LOOP,
                GfcNodeTypeEnum.BREAK,
                GfcNodeTypeEnum.CONTINUE,
                GfcNodeTypeEnum.THROW,
                GfcNodeTypeEnum.SWITCH,
                GfcNodeTypeEnum.CASE,
                GfcNodeTypeEnum.CASE_BLOCK,
                GfcNodeTypeEnum.TRY,
                GfcNodeTypeEnum.CATCH,
                GfcNodeTypeEnum.FINALLY,
                GfcNodeTypeEnum.TERNARY
        );

        for (GfcNodeTypeEnum type : advancedTypes) {
            GfcNode node = new GfcNode(UUID.randomUUID(), "N_" + type.name(), type.name(), type, 10, 12);

            assertEquals(type, node.getType());
            assertEquals(10, node.getStartLine());
            assertEquals(12, node.getEndLine());
        }
    }

    @Test
    void shouldAcceptAdvancedControlFlowEdgeTypes() {
        List<GfcEdgeTypeEnum> advancedTypes = List.of(
                GfcEdgeTypeEnum.LOOP_BODY,
                GfcEdgeTypeEnum.LOOP_EXIT,
                GfcEdgeTypeEnum.CASE_BRANCH,
                GfcEdgeTypeEnum.DEFAULT_BRANCH,
                GfcEdgeTypeEnum.TRY_BRANCH,
                GfcEdgeTypeEnum.CATCH_BRANCH,
                GfcEdgeTypeEnum.FINALLY_BRANCH,
                GfcEdgeTypeEnum.BREAK_FLOW,
                GfcEdgeTypeEnum.CONTINUE_FLOW,
                GfcEdgeTypeEnum.THROW_FLOW
        );

        for (GfcEdgeTypeEnum type : advancedTypes) {
            GfcEdge edge = new GfcEdge(UUID.randomUUID(), "N1", "N2", type, type.name());

            assertEquals(type, edge.getType());
            assertEquals(type.name(), edge.getLabel());
        }
    }
}
