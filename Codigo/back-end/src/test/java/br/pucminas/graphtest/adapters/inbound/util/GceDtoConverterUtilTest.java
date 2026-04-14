package br.pucminas.graphtest.adapters.inbound.util;

import br.pucminas.graphtest.adapters.inbound.dto.gce.GceDTO;
import br.pucminas.graphtest.application.domain.gce.enums.GceEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.GceNodeTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.GceOperatorTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.RestrictionTypeEnum;
import br.pucminas.graphtest.application.port.input.gce.records.GceEdgeOutput;
import br.pucminas.graphtest.application.port.input.gce.records.GceNodeOutput;
import br.pucminas.graphtest.application.port.input.gce.records.GceOutput;
import br.pucminas.graphtest.application.port.input.gce.records.GceRestrictionOutput;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GceDtoConverterUtilTest {

    @Test
    void shouldConvertGceOutputWithAuditFieldsInAllNestedEntities() {
        LocalDateTime graphCreatedAt = LocalDateTime.now().minusDays(3);
        LocalDateTime graphUpdatedAt = LocalDateTime.now().minusDays(1);
        LocalDateTime nodeCreatedAt = LocalDateTime.now().minusDays(2);
        LocalDateTime nodeUpdatedAt = LocalDateTime.now().minusHours(6);
        LocalDateTime edgeCreatedAt = LocalDateTime.now().minusDays(2);
        LocalDateTime edgeUpdatedAt = LocalDateTime.now().minusHours(5);
        LocalDateTime restrictionCreatedAt = LocalDateTime.now().minusDays(2);
        LocalDateTime restrictionUpdatedAt = LocalDateTime.now().minusHours(4);

        GceOutput output = new GceOutput(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Grafo teste",
                "Descricao",
                true,
                graphCreatedAt,
                graphUpdatedAt,
                List.of(new GceNodeOutput(
                        UUID.randomUUID(),
                        "C1",
                        "Causa 1",
                        GceNodeTypeEnum.CAUSE,
                        null,
                        nodeCreatedAt,
                        nodeUpdatedAt
                )),
                List.of(new GceEdgeOutput(
                        UUID.randomUUID(),
                        "C1",
                        "E1",
                        GceEdgeTypeEnum.IDENTITY,
                        edgeCreatedAt,
                        edgeUpdatedAt
                )),
                List.of(new GceRestrictionOutput(
                        UUID.randomUUID(),
                        RestrictionTypeEnum.REQUIRE,
                        List.of("C1", "C2"),
                        restrictionCreatedAt,
                        restrictionUpdatedAt
                ))
        );

        GceDTO dto = GceDtoConverterUtil.toDto(output);

        assertEquals(graphCreatedAt, dto.createdAt());
        assertEquals(graphUpdatedAt, dto.updatedAt());
        assertEquals(nodeCreatedAt, dto.nodes().getFirst().createdAt());
        assertEquals(nodeUpdatedAt, dto.nodes().getFirst().updatedAt());
        assertEquals(edgeCreatedAt, dto.edges().getFirst().createdAt());
        assertEquals(edgeUpdatedAt, dto.edges().getFirst().updatedAt());
        assertEquals(restrictionCreatedAt, dto.restrictions().getFirst().createdAt());
        assertEquals(restrictionUpdatedAt, dto.restrictions().getFirst().updatedAt());
    }
}
