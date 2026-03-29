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
        GceNode cause = GceNode.cause(UUID.randomUUID(), "C1", "Causa 1");
        GceNode effect = GceNode.effect(UUID.randomUUID(), "E1", "Efeito 1");
        Gce gce = new Gce(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "GCE 1",
                "Descricao",
                false,
                List.of(cause, effect),
                List.of(),
                List.of()
        );

        GceNode operator = GceNode.operator(UUID.randomUUID(), "O1", "Operador 1", GceOperatorTypeEnum.AND);
        GceEdge edgeOne = new GceEdge(UUID.randomUUID(), cause.getCode(), operator.getCode(), GceEdgeTypeEnum.IDENTITY);
        GceEdge edgeTwo = new GceEdge(UUID.randomUUID(), effect.getCode(), operator.getCode(), GceEdgeTypeEnum.IDENTITY);

        gce.addNode(operator);
        gce.addEdge(edgeOne);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> gce.addEdge(edgeTwo));
        assertTrue(exception.getMessage().contains("Efeito nao pode ser origem"));
    }

    @Test
    void shouldPreventDuplicateNodeCodeWhenAddingNode() {
        GceNode cause = GceNode.cause(UUID.randomUUID(), "C1", "Causa 1");
        Gce gce = new Gce(
                UUID.randomUUID(),
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
                () -> gce.addNode(GceNode.effect(UUID.randomUUID(), "C1", "Efeito duplicado"))
        );

        assertTrue(exception.getMessage().contains("codigo"));
    }

    @Test
    void shouldPreventNodeRemovalWhenStillReferenced() {
        GceNode cause = GceNode.cause(UUID.randomUUID(), "C1", "Causa 1");
        GceNode operator = GceNode.operator(UUID.randomUUID(), "O1", "Operador 1", GceOperatorTypeEnum.AND);
        GceNode effect = GceNode.effect(UUID.randomUUID(), "E1", "Efeito 1");
        GceEdge edgeOne = new GceEdge(UUID.randomUUID(), cause.getCode(), operator.getCode(), GceEdgeTypeEnum.IDENTITY);
        GceEdge edgeTwo = new GceEdge(UUID.randomUUID(), operator.getCode(), effect.getCode(), GceEdgeTypeEnum.IDENTITY);
        Gce gce = new Gce(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "GCE 1",
                null,
                false,
                List.of(cause, operator, effect),
                List.of(edgeOne, edgeTwo),
                List.of()
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> gce.removeNode(cause.getCode()));
        assertTrue(exception.getMessage().contains("arestas associadas"));
    }

    @Test
    void shouldRollbackNodeReplacementWhenItBreaksRestrictionTyping() {
        UUID causeId = UUID.randomUUID();
        GceNode cause = GceNode.cause(causeId, "C1", "Causa 1");
        GceNode otherCause = GceNode.cause(UUID.randomUUID(), "C2", "Causa 2");
        GceRestriction restriction = new GceRestriction(
                UUID.randomUUID(),
                RestrictionTypeEnum.EXCLUSIVE,
                List.of(cause.getCode(), otherCause.getCode())
        );
        Gce gce = new Gce(
                UUID.randomUUID(),
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
                () -> gce.replaceNode(GceNode.effect(causeId, "C1", "Agora efeito"))
        );

        assertTrue(exception.getMessage().contains("CAUSE"));
        assertTrue(gce.findNode(cause.getCode()).orElseThrow().isCause());
    }

    @Test
    void shouldUpdateDetailsAndSelectionState() {
        GceNode cause = GceNode.cause(UUID.randomUUID(), "C1", "Causa 1");
        GceNode operator = GceNode.operator(UUID.randomUUID(), "O1", "Operador 1", GceOperatorTypeEnum.AND);
        GceNode effect = GceNode.effect(UUID.randomUUID(), "E1", "Efeito 1");
        Gce gce = new Gce(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Nome inicial",
                "Descricao inicial",
                false,
                List.of(cause, operator, effect),
                List.of(
                        new GceEdge(UUID.randomUUID(), cause.getCode(), operator.getCode(), GceEdgeTypeEnum.IDENTITY),
                        new GceEdge(UUID.randomUUID(), operator.getCode(), effect.getCode(), GceEdgeTypeEnum.IDENTITY)
                ),
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

    @Test
    void shouldRejectDirectConnectionFromCauseToEffect() {
        GceNode cause = GceNode.cause(UUID.randomUUID(), "C1", "Causa 1");
        GceNode effect = GceNode.effect(UUID.randomUUID(), "E1", "Efeito 1");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Gce(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "GCE 1",
                        null,
                        false,
                        List.of(cause, effect),
                        List.of(new GceEdge(UUID.randomUUID(), cause.getCode(), effect.getCode(), GceEdgeTypeEnum.IDENTITY)),
                        List.of()
                )
        );

        assertTrue(exception.getMessage().contains("Causa so pode se conectar a operador"));
    }

    @Test
    void shouldRejectEffectIncomingEdgeWhenSourceIsNotOperator() {
        GceNode cause = GceNode.cause(UUID.randomUUID(), "C1", "Causa 1");
        GceNode effect = GceNode.effect(UUID.randomUUID(), "E1", "Efeito 1");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Gce(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "GCE 1",
                        null,
                        false,
                        List.of(cause, effect),
                        List.of(new GceEdge(UUID.randomUUID(), cause.getCode(), effect.getCode(), GceEdgeTypeEnum.NEGATED)),
                        List.of()
                )
        );

        assertTrue(exception.getMessage().contains("Causa so pode se conectar a operador"));
    }

    @Test
    void shouldRejectOperatorWithMoreThanTwoIncomingEdges() {
        GceNode causeOne = GceNode.cause(UUID.randomUUID(), "C1", "Causa 1");
        GceNode causeTwo = GceNode.cause(UUID.randomUUID(), "C2", "Causa 2");
        GceNode causeThree = GceNode.cause(UUID.randomUUID(), "C3", "Causa 3");
        GceNode operator = GceNode.operator(UUID.randomUUID(), "O1", "Operador 1", GceOperatorTypeEnum.OR);
        GceNode effect = GceNode.effect(UUID.randomUUID(), "E1", "Efeito 1");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Gce(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "GCE 1",
                        null,
                        false,
                        List.of(causeOne, causeTwo, causeThree, operator, effect),
                        List.of(
                                new GceEdge(UUID.randomUUID(), "C1", "O1", GceEdgeTypeEnum.IDENTITY),
                                new GceEdge(UUID.randomUUID(), "C2", "O1", GceEdgeTypeEnum.IDENTITY),
                                new GceEdge(UUID.randomUUID(), "C3", "O1", GceEdgeTypeEnum.IDENTITY),
                                new GceEdge(UUID.randomUUID(), "O1", "E1", GceEdgeTypeEnum.IDENTITY)
                        ),
                        List.of()
                )
        );

        assertTrue(exception.getMessage().contains("no maximo duas arestas de entrada"));
    }
}
