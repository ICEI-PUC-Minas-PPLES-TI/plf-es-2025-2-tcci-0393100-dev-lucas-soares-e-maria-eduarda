package br.pucminas.graphtest.application.domain;

import br.pucminas.graphtest.application.domain.enums.GceEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.enums.GceOperatorTypeEnum;
import br.pucminas.graphtest.application.domain.enums.RestrictionTypeEnum;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GceTest {

    @Test
    void shouldAddNodeEdgeAndRestrictionThroughAggregateOperations() {
        GceNode cause = GceNode.cause(1L, "C1", "Causa 1");
        GceNode effect = GceNode.effect(2L, "E1", "Efeito 1");
        Gce gce = new Gce(
                10L,
                UUID.randomUUID(),
                "GCE 1",
                "Descricao",
                false,
                List.of(cause, effect),
                List.of(),
                List.of()
        );

        GceNode operator = GceNode.operator(3L, "O1", "Operador 1", GceOperatorTypeEnum.AND);
        GceEdge edgeOne = new GceEdge(11L, cause.getCode(), operator.getCode(), GceEdgeTypeEnum.IDENTITY);
        GceEdge edgeTwo = new GceEdge(12L, effect.getCode(), operator.getCode(), GceEdgeTypeEnum.IDENTITY);

        gce.addNode(operator);
        gce.addEdge(edgeOne);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> gce.addEdge(edgeTwo));
        assertTrue(exception.getMessage().contains("Efeito nao pode ser origem"));
    }

    @Test
    void shouldPreventDuplicateNodeCodeWhenAddingNode() {
        GceNode cause = GceNode.cause(1L, "C1", "Causa 1");
        Gce gce = new Gce(
                10L,
                UUID.randomUUID(),
                "GCE 1",
                null,
                false,
                List.of(cause),
                List.of(),
                List.of()
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gce.addNode(GceNode.effect(2L, "C1", "Efeito duplicado"))
        );

        assertTrue(exception.getMessage().contains("codigo"));
    }

    @Test
    void shouldPreventNodeRemovalWhenStillReferenced() {
        GceNode cause = GceNode.cause(1L, "C1", "Causa 1");
        GceNode effect = GceNode.effect(2L, "E1", "Efeito 1");
        GceEdge edge = new GceEdge(11L, cause.getCode(), effect.getCode(), GceEdgeTypeEnum.IDENTITY);
        Gce gce = new Gce(
                10L,
                UUID.randomUUID(),
                "GCE 1",
                null,
                false,
                List.of(cause, effect),
                List.of(edge),
                List.of()
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> gce.removeNode(cause.getCode()));
        assertTrue(exception.getMessage().contains("arestas associadas"));
    }

    @Test
    void shouldRollbackNodeReplacementWhenItBreaksRestrictionTyping() {
        GceNode cause = GceNode.cause(1L, "C1", "Causa 1");
        GceNode otherCause = GceNode.cause(2L, "C2", "Causa 2");
        GceRestriction restriction = new GceRestriction(
                20L,
                RestrictionTypeEnum.EXCLUSIVE,
                List.of(cause.getCode(), otherCause.getCode())
        );
        Gce gce = new Gce(
                10L,
                UUID.randomUUID(),
                "GCE 1",
                null,
                false,
                List.of(cause, otherCause),
                List.of(),
                List.of(restriction)
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gce.replaceNode(GceNode.effect(1L, "C1", "Agora efeito"))
        );

        assertTrue(exception.getMessage().contains("CAUSE"));
        assertTrue(gce.findNode(cause.getCode()).orElseThrow().isCause());
    }

    @Test
    void shouldUpdateDetailsAndSelectionState() {
        GceNode cause = GceNode.cause(1L, "C1", "Causa 1");
        Gce gce = new Gce(
                10L,
                UUID.randomUUID(),
                "Nome inicial",
                "Descricao inicial",
                false,
                List.of(cause),
                List.of(),
                List.of()
        );

        gce.updateDetails(" Nome final ", " Descricao final ");
        gce.select();

        assertEquals("Nome final", gce.getName());
        assertEquals("Descricao final", gce.getDescription());
        assertTrue(gce.isSelected());

        gce.unselect();
        assertFalse(gce.isSelected());
    }
}
