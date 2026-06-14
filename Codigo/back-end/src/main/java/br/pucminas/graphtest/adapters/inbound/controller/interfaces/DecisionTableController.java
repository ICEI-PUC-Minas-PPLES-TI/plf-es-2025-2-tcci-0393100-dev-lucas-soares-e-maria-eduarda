package br.pucminas.graphtest.adapters.inbound.controller.interfaces;

import br.pucminas.graphtest.adapters.inbound.dto.decisiontable.DecisionTableDTO;
import br.pucminas.graphtest.adapters.inbound.dto.decisiontable.GenerateFunctionalTestSignatureResponseDTO;
import br.pucminas.graphtest.adapters.inbound.dto.decisiontable.UpdateDecisionTableDetailsDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.DECISION_TABLE_BY_GCE;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.DECISION_TABLE_ID;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.DECISION_TABLE_GENERATE;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.DECISION_TABLE_PREVIEW;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.DECISION_TABLE_REFRESH;

public interface DecisionTableController {

    @PostMapping(DECISION_TABLE_GENERATE)
    ResponseEntity<DecisionTableDTO> create(@PathVariable UUID projectId, @PathVariable UUID gceId);

    @GetMapping(DECISION_TABLE_ID)
    ResponseEntity<DecisionTableDTO> findById(@PathVariable UUID projectId,
                                              @PathVariable("decisionTableId") UUID id);

    @GetMapping(DECISION_TABLE_ID + "/status")
    ResponseEntity<Boolean> findStatusById(@PathVariable UUID projectId,
                                           @PathVariable("decisionTableId") UUID id);

    @PatchMapping(DECISION_TABLE_ID)
    ResponseEntity<DecisionTableDTO> patchDetails(@PathVariable UUID projectId,
                                                  @PathVariable("decisionTableId") UUID id,
                                                  @RequestBody UpdateDecisionTableDetailsDTO decisionTable);

    @GetMapping(DECISION_TABLE_BY_GCE)
    ResponseEntity<DecisionTableDTO> findByGceId(@PathVariable UUID projectId, @PathVariable UUID gceId);

    @GetMapping(DECISION_TABLE_PREVIEW)
    ResponseEntity<DecisionTableDTO> preview(@PathVariable UUID projectId, @PathVariable UUID gceId);

    @PutMapping(DECISION_TABLE_REFRESH)
    ResponseEntity<DecisionTableDTO> refresh(@PathVariable UUID projectId, @PathVariable UUID gceId);

    @GetMapping
    ResponseEntity<List<DecisionTableDTO>> listByProject(@PathVariable UUID projectId);

    @DeleteMapping(DECISION_TABLE_ID)
    ResponseEntity<Map<String, Object>> delete(@PathVariable UUID projectId,
                                               @PathVariable("decisionTableId") UUID id);

    @GetMapping(DECISION_TABLE_ID + "/assinatura-teste-funcional")
    ResponseEntity<GenerateFunctionalTestSignatureResponseDTO> generateFunctionalTestSignature(
            @PathVariable UUID projectId,
            @PathVariable UUID decisionTableId
    );
}
