package br.pucminas.graphtest.adapters.inbound.controller.interfaces;

import br.pucminas.graphtest.adapters.inbound.dto.gfc.CreateGfcDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.CreateGfcResponseDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.CyclomaticComplexityResponseDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.DeleteGfcResponseDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcSummaryDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.PreviewGfcDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.*;

public interface GfcController {

    @PostMapping
    ResponseEntity<CreateGfcResponseDTO> create(@Validated @RequestBody CreateGfcDTO request);

    @GetMapping("/{gfcId}")
    ResponseEntity<GfcDTO> findById(@PathVariable UUID gfcId);

    @DeleteMapping("/{gfcId}")
    ResponseEntity<DeleteGfcResponseDTO> delete(@PathVariable UUID gfcId);

    @GetMapping(GFC_PROJECT)
    ResponseEntity<List<GfcSummaryDTO>> listByProject(@PathVariable UUID projectId);

    @PostMapping(GFC_PREVIEW)
    ResponseEntity<GfcDTO> preview(@Validated @RequestBody PreviewGfcDTO request);

    @GetMapping(GFC_CYCLOMATIC_COMPLEXITY)
    ResponseEntity<CyclomaticComplexityResponseDTO> calculateCyclomaticComplexity(@PathVariable UUID gfcId);
}
