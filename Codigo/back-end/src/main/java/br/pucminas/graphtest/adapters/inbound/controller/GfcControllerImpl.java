package br.pucminas.graphtest.adapters.inbound.controller;

import br.pucminas.graphtest.adapters.inbound.controller.interfaces.GfcController;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.CreateGfcDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.CreateGfcResponseDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.CyclomaticComplexityResponseDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.DeleteGfcResponseDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GenerateStructuralTestSignatureResponseDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcSummaryDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.PreviewGfcDTO;
import br.pucminas.graphtest.adapters.inbound.util.GfcDtoConverterUtil;
import br.pucminas.graphtest.application.port.input.gfc.CreateGfcUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.CalculateCyclomaticComplexityUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.DeleteGfcUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.FindGfcByIdUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.GenerateStructuralTestSignatureUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.ListGfcByProjectUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.PreviewGfcUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.records.CreateGfcOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.CyclomaticComplexityOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcSummaryOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GenerateStructuralTestSignatureOutput;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static br.pucminas.graphtest.adapters.inbound.util.GfcDtoConverterUtil.toCreateGfcInput;
import static br.pucminas.graphtest.adapters.inbound.util.GfcDtoConverterUtil.toPreviewInput;
import static br.pucminas.graphtest.adapters.inbound.util.GfcDtoConverterUtil.toDto;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.*;
import static br.pucminas.graphtest.shared.LogTopicsUtil.GFC_CONTROLLER;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j(topic = GFC_CONTROLLER)
@RestController
@Validated
@RequestMapping(GFC)
@AllArgsConstructor
public class GfcControllerImpl implements GfcController {

    private static final String MSG_GFC_REMOVIDO = "Grafo de Fluxo de Controle removido com sucesso";

    private final CreateGfcUseCasePort createGfcUseCasePort;
    private final DeleteGfcUseCasePort deleteGfcUseCasePort;
    private final FindGfcByIdUseCasePort findGfcByIdUseCasePort;
    private final ListGfcByProjectUseCasePort listGfcByProjectUseCasePort;
    private final PreviewGfcUseCasePort previewGfcUseCasePort;
    private final CalculateCyclomaticComplexityUseCasePort calculateCyclomaticComplexityUseCasePort;
    private final GenerateStructuralTestSignatureUseCasePort generateStructuralTestSignatureUseCasePort;

    @Override
    @PostMapping
    public ResponseEntity<CreateGfcResponseDTO> create(@Validated @RequestBody CreateGfcDTO request) {
        log.info(">>> criarGfc: recebendo requisicao para criar GFC persistido");

        CreateGfcOutput output = createGfcUseCasePort.execute(toCreateGfcInput(request));
        return ResponseEntity.created(URI.create(GFC + "/" + output.gfcId()))
                .body(toDto(output, CREATED.value()));
    }

    @Override
    @GetMapping("/{gfcId}")
    public ResponseEntity<GfcDTO> findById(@PathVariable UUID gfcId) {
        log.info(">>> buscarGfcPorId: recebendo requisicao para buscar GFC persistido");

        GfcOutput output = findGfcByIdUseCasePort.execute(gfcId);
        return ResponseEntity.ok(toDto(output));
    }

    @Override
    @DeleteMapping("/{gfcId}")
    public ResponseEntity<DeleteGfcResponseDTO> delete(@PathVariable UUID gfcId) {
        log.info(">>> removerGfc: recebendo requisicao para remover GFC persistido");

        deleteGfcUseCasePort.execute(gfcId);
        return ResponseEntity.ok(new DeleteGfcResponseDTO(MSG_GFC_REMOVIDO, OK.value()));
    }

    @Override
    @GetMapping(GFC_PROJECT)
    public ResponseEntity<List<GfcSummaryDTO>> listByProject(@PathVariable UUID projectId) {
        log.info(">>> listarGfcsPorProjeto: recebendo requisicao para listar GFCs por projeto");

        List<GfcSummaryOutput> outputs = listGfcByProjectUseCasePort.execute(projectId);
        return ResponseEntity.ok(outputs.stream().map(GfcDtoConverterUtil::toSummaryDto).toList());
    }

    @Override
    @PostMapping(GFC_PREVIEW)
    public ResponseEntity<GfcDTO> preview(@Validated @RequestBody PreviewGfcDTO request) {
        log.info(">>> preVisualizar: recebendo requisicao para pre-visualizar GFC");

        GfcOutput graph = previewGfcUseCasePort.execute(toPreviewInput(request));
        return ResponseEntity.ok(toDto(graph));
    }

    @Override
    @GetMapping(GFC_CYCLOMATIC_COMPLEXITY)
    public ResponseEntity<CyclomaticComplexityResponseDTO> calculateCyclomaticComplexity(@PathVariable UUID gfcId) {
        log.info(">>> calcularComplexidadeCiclomatica: recebendo requisicao para calcular complexidade ciclomatica");

        CyclomaticComplexityOutput output = calculateCyclomaticComplexityUseCasePort.execute(gfcId);
        return ResponseEntity.ok(toDto(output));
    }

    @Override
    @GetMapping(GFC_STRUCTURAL_TEST_SIGNATURE)
    public ResponseEntity<GenerateStructuralTestSignatureResponseDTO> generateStructuralTestSignature(@PathVariable UUID gfcId) {
        log.info(">>> gerarAssinaturaTesteEstrutural: recebendo requisicao para gerar assinaturas de teste estrutural");

        GenerateStructuralTestSignatureOutput output = generateStructuralTestSignatureUseCasePort.execute(gfcId);
        return ResponseEntity.ok(toDto(output));
    }
}
