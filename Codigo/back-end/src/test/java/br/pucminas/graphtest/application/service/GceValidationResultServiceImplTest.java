package br.pucminas.graphtest.application.service;

import br.pucminas.graphtest.application.domain.Gce;
import br.pucminas.graphtest.application.domain.GceEdge;
import br.pucminas.graphtest.application.domain.GceNode;
import br.pucminas.graphtest.application.domain.GceRestriction;
import br.pucminas.graphtest.application.domain.enums.GceEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.enums.GceOperatorTypeEnum;
import br.pucminas.graphtest.application.domain.enums.RestrictionTypeEnum;
import br.pucminas.graphtest.application.port.input.gce.records.ValidationGceOutput;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GceValidationResultServiceImplTest {

    private final GceValidationResultServiceImpl service = new GceValidationResultServiceImpl();

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
                        GceNode.effect(UUID.randomUUID(), "E1", "Efeito 1"),
                        GceNode.effect(UUID.randomUUID(), "E2", "Efeito 2")
                ),
                List.of(
                        new GceEdge(UUID.randomUUID(), "C1", "E1", GceEdgeTypeEnum.IDENTITY),
                        new GceEdge(UUID.randomUUID(), "C1", "E2", GceEdgeTypeEnum.IDENTITY)
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
