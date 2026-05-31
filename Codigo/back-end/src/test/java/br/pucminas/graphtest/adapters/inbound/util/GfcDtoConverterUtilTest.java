package br.pucminas.graphtest.adapters.inbound.util;

import br.pucminas.graphtest.adapters.inbound.dto.gfc.CreateGfcSourceFileResponseDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.CyclomaticComplexityResponseDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.CreateGfcDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.CreateGfcResponseDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GenerateStructuralTestSignatureResponseDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcSourceFileDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.PreviewGfcDTO;
import br.pucminas.graphtest.application.domain.gfc.enums.GfcEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.gfc.enums.GfcNodeTypeEnum;
import br.pucminas.graphtest.application.port.input.gfc.records.CreateGfcSourceFileInput;
import br.pucminas.graphtest.application.port.input.gfc.records.CreateGfcSourceFileOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.CyclomaticComplexityOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.CreateGfcInput;
import br.pucminas.graphtest.application.port.input.gfc.records.CreateGfcOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcEdgeOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GenerateStructuralTestSignatureOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcNodeOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceCodeOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceFileOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceMethodDetailsOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceMethodOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcSummaryOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.PreviewGfcInput;
import br.pucminas.graphtest.application.port.input.gfc.records.StructuralTestMethodSignatureOutput;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
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
    void shouldConvertSourceFileUploadToInputPortRecord() {
        UUID projectId = UUID.randomUUID();

        CreateGfcSourceFileInput input = GfcDtoConverterUtil.toCreateSourceFileInput(
                projectId,
                "Exemplo.java",
                "class Exemplo {}"
        );

        assertEquals(projectId, input.projectId());
        assertEquals("Exemplo.java", input.fileName());
        assertEquals("class Exemplo {}", input.content());
    }

    @Test
    void shouldConvertCreateGfcRequestToInputPortRecord() {
        UUID projectId = UUID.randomUUID();
        UUID sourceFileId = UUID.randomUUID();
        CreateGfcDTO dto = new CreateGfcDTO(projectId, sourceFileId, "int soma(int a, int b)", "GFC", "Descricao");

        CreateGfcInput input = GfcDtoConverterUtil.toCreateGfcInput(dto);

        assertEquals(projectId, input.projectId());
        assertEquals(sourceFileId, input.sourceFileId());
        assertEquals("int soma(int a, int b)", input.methodSignature());
        assertEquals("GFC", input.name());
        assertEquals("Descricao", input.description());
    }

    @Test
    void shouldConvertOutputPortRecordToResponseDto() {
        UUID graphId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID sourceFileId = UUID.randomUUID();
        UUID nodeId = UUID.randomUUID();
        UUID edgeId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        GfcOutput output = new GfcOutput(
                graphId,
                projectId,
                sourceFileId,
                "void m()",
                "GFC",
                "Descricao",
                "Java",
                createdAt,
                List.of(new GfcNodeOutput(nodeId, "N1", "int x = 1;", GfcNodeTypeEnum.STATEMENT, 1, 1)),
                List.of(new GfcEdgeOutput(edgeId, "N0", "N1", GfcEdgeTypeEnum.SEQUENTIAL, null))
        );

        GfcDTO dto = GfcDtoConverterUtil.toDto(output);

        assertEquals(graphId, dto.id());
        assertEquals(projectId, dto.projectId());
        assertEquals(sourceFileId, dto.sourceFileId());
        assertEquals("void m()", dto.methodSignature());
        assertEquals("GFC", dto.name());
        assertEquals("Java", dto.language());
        assertEquals(createdAt, dto.createdAt());
        assertEquals(nodeId, dto.nodes().getFirst().id());
        assertEquals(GfcNodeTypeEnum.STATEMENT, dto.nodes().getFirst().type());
        assertEquals(edgeId, dto.edges().getFirst().id());
        assertEquals(GfcEdgeTypeEnum.SEQUENTIAL, dto.edges().getFirst().type());
    }

    @Test
    void shouldConvertAdvancedControlFlowEnumsToResponseDto() {
        UUID graphId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID sourceFileId = UUID.randomUUID();
        UUID nodeId = UUID.randomUUID();
        UUID edgeId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        GfcOutput output = new GfcOutput(
                graphId,
                projectId,
                sourceFileId,
                "void m()",
                "GFC",
                "Descricao",
                "Java",
                createdAt,
                List.of(new GfcNodeOutput(nodeId, "N1", "while (ativo)", GfcNodeTypeEnum.LOOP, 3, 3)),
                List.of(new GfcEdgeOutput(edgeId, "N1", "N2", GfcEdgeTypeEnum.LOOP_BODY, "body"))
        );

        GfcDTO dto = GfcDtoConverterUtil.toDto(output);

        assertEquals(GfcNodeTypeEnum.LOOP, dto.nodes().getFirst().type());
        assertEquals(GfcEdgeTypeEnum.LOOP_BODY, dto.edges().getFirst().type());
    }

    @Test
    void shouldConvertCyclomaticComplexityOutputToResponseDto() {
        UUID gfcId = UUID.randomUUID();
        CyclomaticComplexityOutput output = new CyclomaticComplexityOutput(
                gfcId,
                10,
                15,
                6,
                7,
                7,
                "V(g) = a - n + 2",
                "V(G) = P + 1",
                List.of("Aviso")
        );

        CyclomaticComplexityResponseDTO dto = GfcDtoConverterUtil.toDto(output);

        assertEquals(gfcId, dto.gfcId());
        assertEquals(10, dto.nodesCount());
        assertEquals(15, dto.edgesCount());
        assertEquals(6, dto.predicateNodesCount());
        assertEquals(7, dto.cyclomaticComplexityByEdgesAndNodes());
        assertEquals(7, dto.cyclomaticComplexityByPredicateNodes());
        assertEquals("V(g) = a - n + 2", dto.formulaByEdgesAndNodes());
        assertEquals("V(G) = P + 1", dto.formulaByPredicateNodes());
        assertEquals(List.of("Aviso"), dto.warnings());
    }

    @Test
    void shouldConvertStructuralTestSignatureOutputToResponseDto() {
        UUID gfcId = UUID.randomUUID();
        String code = "@Test\nvoid teste01() {\n\n}";
        GenerateStructuralTestSignatureOutput output = new GenerateStructuralTestSignatureOutput(
                gfcId,
                "void executar()",
                1,
                List.of(new StructuralTestMethodSignatureOutput("teste01", code)),
                code
        );

        GenerateStructuralTestSignatureResponseDTO dto = GfcDtoConverterUtil.toDto(output);

        assertEquals(gfcId, dto.gfcId());
        assertEquals("void executar()", dto.methodSignature());
        assertEquals(1, dto.cyclomaticComplexity());
        assertEquals("teste01", dto.testMethods().getFirst().methodName());
        assertEquals(code, dto.generatedCode());
    }

    @Test
    void shouldConvertSourceMethodOutputToResponseDto() {
        GfcSourceMethodOutput output = new GfcSourceMethodOutput("soma", "int soma(int a, int b)", 1, 1);

        var dto = GfcDtoConverterUtil.toDto(output);

        assertEquals("soma", dto.name());
        assertEquals("int soma(int a, int b)", dto.signature());
        assertEquals(1, dto.startLine());
        assertEquals(1, dto.endLine());
    }

    @Test
    void shouldConvertSourceMethodDetailsOutputToResponseDto() {
        GfcSourceMethodDetailsOutput output = new GfcSourceMethodDetailsOutput(
                "soma",
                "int soma(int a, int b)",
                2,
                4,
                "int soma(int a, int b) {\n    return a + b;\n}"
        );

        var dto = GfcDtoConverterUtil.toDto(output);

        assertEquals("soma", dto.name());
        assertEquals("int soma(int a, int b)", dto.signature());
        assertEquals(2, dto.startLine());
        assertEquals(4, dto.endLine());
        assertEquals("int soma(int a, int b) {\n    return a + b;\n}", dto.sourceCode());
    }

    @Test
    void shouldConvertGfcSummaryOutputToResponseDto() {
        UUID gfcId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID sourceFileId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        GfcSummaryOutput output = new GfcSummaryOutput(
                gfcId,
                projectId,
                sourceFileId,
                "int soma(int a, int b)",
                "GFC soma",
                "Descricao",
                "Java",
                createdAt
        );

        var dto = GfcDtoConverterUtil.toSummaryDto(output);

        assertEquals(gfcId, dto.id());
        assertEquals(projectId, dto.projectId());
        assertEquals(sourceFileId, dto.sourceFileId());
        assertEquals("int soma(int a, int b)", dto.methodSignature());
        assertEquals("GFC soma", dto.name());
        assertEquals("Descricao", dto.description());
        assertEquals("Java", dto.language());
        assertEquals(createdAt, dto.createdAt());
    }

    @Test
    void shouldConvertSourceCodeOutputToResponseDto() {
        GfcSourceCodeOutput output = new GfcSourceCodeOutput("class Exemplo {}");

        var dto = GfcDtoConverterUtil.toDto(output);

        assertEquals("class Exemplo {}", dto.sourceCode());
    }

    @Test
    void shouldConvertSourceFileOutputToResponseDto() {
        UUID sourceFileId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = createdAt.plusMinutes(1);
        GfcSourceFileOutput output = new GfcSourceFileOutput(
                sourceFileId,
                projectId,
                "Exemplo.java",
                "Java",
                createdAt,
                updatedAt
        );

        GfcSourceFileDTO dto = GfcDtoConverterUtil.toDto(output);

        assertEquals(sourceFileId, dto.id());
        assertEquals(projectId, dto.projectId());
        assertEquals("Exemplo.java", dto.fileName());
        assertEquals("Java", dto.language());
        assertEquals(createdAt, dto.createdAt());
        assertEquals(updatedAt, dto.updatedAt());
    }

    @Test
    void shouldConvertCreateSourceFileOutputToResponseDto() {
        UUID sourceFileId = UUID.randomUUID();

        CreateGfcSourceFileResponseDTO dto = GfcDtoConverterUtil.toDto(
                new CreateGfcSourceFileOutput(sourceFileId),
                201
        );

        assertEquals(sourceFileId, dto.id_arquivo());
        assertEquals("arquivo cadastrado com sucesso", dto.mensagem());
        assertEquals(201, dto.status());
    }

    @Test
    void shouldConvertCreateGfcOutputToResponseDto() {
        UUID gfcId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        CreateGfcResponseDTO dto = GfcDtoConverterUtil.toDto(new CreateGfcOutput(gfcId, createdAt), 201);

        assertEquals(gfcId, dto.id_gfc());
        assertEquals("Grafo de Fluxo de Controle criado com sucesso", dto.mensagem());
        assertEquals(201, dto.status());
        assertEquals(createdAt, dto.createdAt());
    }
}
