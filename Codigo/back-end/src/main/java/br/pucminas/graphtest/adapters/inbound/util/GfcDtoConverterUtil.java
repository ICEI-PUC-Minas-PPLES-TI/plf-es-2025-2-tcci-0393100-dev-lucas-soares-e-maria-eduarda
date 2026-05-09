package br.pucminas.graphtest.adapters.inbound.util;

import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcEdgeDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcNodeDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcSourceMethodDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.PreviewGfcDTO;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcEdgeOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcNodeOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceMethodOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.ListGfcSourceMethodsInput;
import br.pucminas.graphtest.application.port.input.gfc.records.PreviewGfcInput;
import lombok.experimental.UtilityClass;

/**
 * Conversoes entre DTOs inbound e contratos de entrada do modulo GFC.
 */
@UtilityClass
public class GfcDtoConverterUtil {

    public static ListGfcSourceMethodsInput toListMethodsInput(String sourceCode) {
        return new ListGfcSourceMethodsInput(sourceCode);
    }

    public static PreviewGfcInput toPreviewInput(PreviewGfcDTO dto) {
        return new PreviewGfcInput(
                dto.projectId(),
                dto.name(),
                dto.description(),
                dto.sourceCode(),
                dto.methodSignature()
        );
    }

    public static GfcDTO toDto(GfcOutput output) {
        return new GfcDTO(
                output.id(),
                output.projectId(),
                output.name(),
                output.description(),
                output.sourceCode(),
                output.language(),
                output.nodes().stream().map(GfcDtoConverterUtil::toDto).toList(),
                output.edges().stream().map(GfcDtoConverterUtil::toDto).toList()
        );
    }

    private static GfcNodeDTO toDto(GfcNodeOutput output) {
        return new GfcNodeDTO(
                output.id(),
                output.code(),
                output.label(),
                output.type(),
                output.startLine(),
                output.endLine()
        );
    }

    private static GfcEdgeDTO toDto(GfcEdgeOutput output) {
        return new GfcEdgeDTO(
                output.id(),
                output.sourceNodeCode(),
                output.targetNodeCode(),
                output.type(),
                output.label()
        );
    }

    public static GfcSourceMethodDTO toDto(GfcSourceMethodOutput output) {
        return new GfcSourceMethodDTO(
                output.name(),
                output.signature(),
                output.startLine(),
                output.endLine()
        );
    }
}
