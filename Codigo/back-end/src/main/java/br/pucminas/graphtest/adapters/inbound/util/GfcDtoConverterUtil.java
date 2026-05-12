package br.pucminas.graphtest.adapters.inbound.util;

import br.pucminas.graphtest.adapters.inbound.dto.gfc.CreateGfcSourceFileResponseDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.CreateGfcDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.CreateGfcResponseDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcEdgeDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcNodeDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcSourceCodeDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcSourceFileDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcSourceMethodDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcSummaryDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.PreviewGfcDTO;
import br.pucminas.graphtest.application.port.input.gfc.records.CreateGfcSourceFileInput;
import br.pucminas.graphtest.application.port.input.gfc.records.CreateGfcSourceFileOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.CreateGfcInput;
import br.pucminas.graphtest.application.port.input.gfc.records.CreateGfcOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcEdgeOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcNodeOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceCodeOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceFileOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceMethodOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcSummaryOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.PreviewGfcInput;
import lombok.experimental.UtilityClass;

import java.util.UUID;

/**
 * Conversoes entre DTOs inbound e contratos de entrada do modulo GFC.
 */
@UtilityClass
public class GfcDtoConverterUtil {

    private static final String MSG_GFC_SOURCE_FILE_CRIADO = "arquivo cadastrado com sucesso";
    private static final String MSG_GFC_CRIADO = "Grafo de Fluxo de Controle criado com sucesso";

    public static CreateGfcInput toCreateGfcInput(CreateGfcDTO dto) {
        return new CreateGfcInput(
                dto.projectId(),
                dto.sourceFileId(),
                dto.methodSignature(),
                dto.name(),
                dto.description()
        );
    }

    public static CreateGfcSourceFileInput toCreateSourceFileInput(UUID projectId, String fileName, String content) {
        return new CreateGfcSourceFileInput(projectId, fileName, content);
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
                output.sourceFileId(),
                output.methodSignature(),
                output.name(),
                output.description(),
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

    public static GfcSummaryDTO toSummaryDto(GfcSummaryOutput output) {
        return new GfcSummaryDTO(
                output.id(),
                output.projectId(),
                output.sourceFileId(),
                output.methodSignature(),
                output.name(),
                output.description(),
                output.language()
        );
    }

    public static GfcSourceCodeDTO toDto(GfcSourceCodeOutput output) {
        return new GfcSourceCodeDTO(output.sourceCode());
    }

    public static GfcSourceFileDTO toDto(GfcSourceFileOutput output) {
        return new GfcSourceFileDTO(
                output.id(),
                output.projectId(),
                output.fileName(),
                output.language(),
                output.createdAt(),
                output.updatedAt()
        );
    }

    public static CreateGfcSourceFileResponseDTO toDto(CreateGfcSourceFileOutput output, Integer status) {
        return new CreateGfcSourceFileResponseDTO(
                output.sourceFileId(),
                MSG_GFC_SOURCE_FILE_CRIADO,
                status
        );
    }

    public static CreateGfcResponseDTO toDto(CreateGfcOutput output, Integer status) {
        return new CreateGfcResponseDTO(
                output.gfcId(),
                MSG_GFC_CRIADO,
                status
        );
    }
}
