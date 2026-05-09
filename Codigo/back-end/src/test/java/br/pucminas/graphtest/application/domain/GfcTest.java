package br.pucminas.graphtest.application.domain;

import br.pucminas.graphtest.application.domain.gfc.enums.GfcEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.gfc.enums.GfcNodeTypeEnum;
import br.pucminas.graphtest.application.domain.gfc.model.Gfc;
import br.pucminas.graphtest.application.domain.gfc.model.GfcEdge;
import br.pucminas.graphtest.application.domain.gfc.model.GfcNode;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GfcTest {

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
                "Metodo calcular",
                "Fluxo principal",
                "void calcular() { }",
                "Java",
                List.of(start, decision, end),
                List.of(edgeOne, edgeTwo)
        );

        assertEquals("Metodo calcular", gfc.getName());
        assertEquals(Gfc.JAVA_LANGUAGE, gfc.getLanguage());
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

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Gfc(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "Metodo",
                        null,
                        "void metodo() { }",
                        "Kotlin",
                        List.of(start),
                        List.of()
                )
        );

        assertTrue(exception.getMessage().contains("Java"));
    }

    @Test
    void shouldRejectDuplicatedNodeCode() {
        GfcNode start = GfcNode.start(UUID.randomUUID(), "N1", "Inicio");
        GfcNode statement = GfcNode.statement(UUID.randomUUID(), "N1", "int x = 1", 2, 2);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Gfc(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "Metodo",
                        null,
                        "void metodo() { }",
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

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Gfc(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "Metodo",
                        null,
                        "void metodo() { }",
                        "Java",
                        List.of(start),
                        List.of(edge)
                )
        );

        assertTrue(exception.getMessage().contains("N2"));
    }

    @Test
    void shouldRejectInvalidNodeLineRange() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new GfcNode(UUID.randomUUID(), "N1", "Comando", GfcNodeTypeEnum.STATEMENT, 10, 9)
        );

        assertTrue(exception.getMessage().contains("linha final"));
    }

    @Test
    void shouldRejectMissingLineForSourceCodeNode() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
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
                "Metodo",
                null,
                null,
                "Java",
                List.of(decision),
                List.of(loop)
        );

        assertEquals("", gfc.getSourceCode());
        assertEquals(1, gfc.outgoingEdges("N1").size());
        assertEquals(1, gfc.incomingEdges("N1").size());
    }
}
