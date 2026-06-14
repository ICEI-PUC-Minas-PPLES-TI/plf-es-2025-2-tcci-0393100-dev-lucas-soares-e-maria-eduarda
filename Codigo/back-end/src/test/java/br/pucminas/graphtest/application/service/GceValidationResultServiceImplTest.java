package br.pucminas.graphtest.application.service;

import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.domain.gce.model.GceEdge;
import br.pucminas.graphtest.application.domain.gce.model.GceNode;
import br.pucminas.graphtest.application.domain.gce.model.GceRestriction;
import br.pucminas.graphtest.application.domain.gce.enums.GceEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.GceOperatorTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.RestrictionTypeEnum;
import br.pucminas.graphtest.application.port.input.gce.records.ValidationGceOutput;
import br.pucminas.graphtest.application.service.gce.GceValidationResultServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GceValidationResultServiceImplTest {

    private final GceValidationResultServiceImpl service = new GceValidationResultServiceImpl();

    @Test
    void shouldValidateSimpleDeterministicGraphWithDirectCauseToEffectConnection() {
        Gce graph = new Gce(
                null,
                UUID.randomUUID(),
                "GCE",
                "Descricao",
                false,
                List.of(
                        GceNode.cause(UUID.randomUUID(), "C1", "Causa"),
                        GceNode.effect(UUID.randomUUID(), "E1", "Efeito")
                ),
                List.of(new GceEdge(UUID.randomUUID(), "C1", "E1", GceEdgeTypeEnum.NEGATED)),
                List.of()
        );

        ValidationGceOutput output = service.validate(graph);

        assertTrue(output.valid());
        assertTrue(output.errors().isEmpty());
    }

    @Test
    void shouldInvalidateGraphWhenMaskRestrictionCanBeViolated() {
        Gce graph = new Gce(
                null,
                UUID.randomUUID(),
                "GCE",
                "Descricao",
                false,
                List.of(
                        GceNode.cause(UUID.randomUUID(), "C1", "Causa"),
                        GceNode.cause(UUID.randomUUID(), "C2", "Causa 2"),
                        GceNode.operator(UUID.randomUUID(), "O1", "Operador 1", GceOperatorTypeEnum.OR),
                        GceNode.operator(UUID.randomUUID(), "O2", "Operador 2", GceOperatorTypeEnum.OR),
                        GceNode.effect(UUID.randomUUID(), "E1", "Efeito 1"),
                        GceNode.effect(UUID.randomUUID(), "E2", "Efeito 2")
                ),
                List.of(
                        new GceEdge(UUID.randomUUID(), "C1", "O1", GceEdgeTypeEnum.IDENTITY),
                        new GceEdge(UUID.randomUUID(), "C2", "O1", GceEdgeTypeEnum.IDENTITY),
                        new GceEdge(UUID.randomUUID(), "C1", "O2", GceEdgeTypeEnum.IDENTITY),
                        new GceEdge(UUID.randomUUID(), "C2", "O2", GceEdgeTypeEnum.IDENTITY),
                        new GceEdge(UUID.randomUUID(), "O1", "E1", GceEdgeTypeEnum.IDENTITY),
                        new GceEdge(UUID.randomUUID(), "O2", "E2", GceEdgeTypeEnum.IDENTITY)
                ),
                List.of(new GceRestriction(UUID.randomUUID(), RestrictionTypeEnum.MASKS, List.of("E1", "E2")))
        );

        ValidationGceOutput output = service.validate(graph);

        assertFalse(output.valid());
        assertTrue(output.errors().stream().anyMatch(error -> "GCE_020".equals(error.code())));
    }

    @Test
    void shouldInvalidateGraphWhenCauseReachesNodeWithConflictingPolarities() {
        Gce graph = new Gce(
                null,
                UUID.randomUUID(),
                "GCE",
                "Descricao",
                false,
                List.of(
                        GceNode.cause(UUID.randomUUID(), "C1", "Causa"),
                        GceNode.operator(UUID.randomUUID(), "O1", "Operador", GceOperatorTypeEnum.OR),
                        GceNode.effect(UUID.randomUUID(), "E1", "Efeito")
                ),
                List.of(
                        new GceEdge(UUID.randomUUID(), "C1", "O1", GceEdgeTypeEnum.IDENTITY),
                        new GceEdge(UUID.randomUUID(), "C1", "O1", GceEdgeTypeEnum.NEGATED),
                        new GceEdge(UUID.randomUUID(), "O1", "E1", GceEdgeTypeEnum.IDENTITY)
                ),
                List.of()
        );

        ValidationGceOutput output = service.validate(graph);

        assertFalse(output.valid());
        assertTrue(output.errors().stream().anyMatch(error -> "GCE_023".equals(error.code())));
    }
}
