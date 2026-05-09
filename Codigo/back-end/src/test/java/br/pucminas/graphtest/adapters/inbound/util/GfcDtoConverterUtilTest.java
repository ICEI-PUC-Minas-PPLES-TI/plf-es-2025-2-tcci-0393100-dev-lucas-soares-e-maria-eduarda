package br.pucminas.graphtest.adapters.inbound.util;

import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.PreviewGfcDTO;
import br.pucminas.graphtest.application.domain.gfc.enums.GfcEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.gfc.enums.GfcNodeTypeEnum;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcEdgeOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcNodeOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.PreviewGfcInput;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GfcDtoConverterUtilTest {

    @Test
    void shouldConvertPreviewRequestToInputPortRecord() {
        UUID projectId = UUID.randomUUID();
        PreviewGfcDTO dto = new PreviewGfcDTO(projectId, "GFC", "Descricao", "int x = 1;", "void m()");

        PreviewGfcInput input = GfcDtoConverterUtil.toPreviewInput(dto);

        assertEquals(projectId, input.projectId());
        assertEquals("GFC", input.name());
        assertEquals("Descricao", input.description());
        assertEquals("int x = 1;", input.sourceCode());
        assertEquals("void m()", input.methodSignature());
    }

    @Test
    void shouldConvertOutputPortRecordToResponseDto() {
        UUID graphId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID nodeId = UUID.randomUUID();
        UUID edgeId = UUID.randomUUID();
        GfcOutput output = new GfcOutput(
                graphId,
                projectId,
                "GFC",
                "Descricao",
                "int x = 1;",
                "Java",
                List.of(new GfcNodeOutput(nodeId, "N1", "int x = 1;", GfcNodeTypeEnum.STATEMENT, 1, 1)),
                List.of(new GfcEdgeOutput(edgeId, "N0", "N1", GfcEdgeTypeEnum.SEQUENTIAL, null))
        );

        GfcDTO dto = GfcDtoConverterUtil.toDto(output);

        assertEquals(graphId, dto.id());
        assertEquals(projectId, dto.projectId());
        assertEquals("GFC", dto.name());
        assertEquals("Java", dto.language());
        assertEquals(nodeId, dto.nodes().getFirst().id());
        assertEquals(GfcNodeTypeEnum.STATEMENT, dto.nodes().getFirst().type());
        assertEquals(edgeId, dto.edges().getFirst().id());
        assertEquals(GfcEdgeTypeEnum.SEQUENTIAL, dto.edges().getFirst().type());
    }
}
