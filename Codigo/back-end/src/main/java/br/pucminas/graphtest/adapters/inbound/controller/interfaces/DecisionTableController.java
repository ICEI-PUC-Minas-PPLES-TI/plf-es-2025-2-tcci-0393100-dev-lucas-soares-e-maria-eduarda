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
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.DECISION_TABLE_GENERATE;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.DECISION_TABLE_FUNCTIONAL_TEST_SIGNATURE;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.DECISION_TABLE_PREVIEW;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.DECISION_TABLE_PROJECT;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.DECISION_TABLE_REFRESH;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.DECISION_TABLE_STATUS;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.ID;

public interface DecisionTableController {

    @PostMapping(DECISION_TABLE_GENERATE)
    ResponseEntity<DecisionTableDTO> create(@PathVariable UUID gceId);

    @GetMapping(ID)
    ResponseEntity<DecisionTableDTO> findById(@PathVariable UUID id);

    @GetMapping(DECISION_TABLE_STATUS)
    ResponseEntity<Boolean> findStatusById(@PathVariable UUID id);

    @PatchMapping(ID)
    ResponseEntity<DecisionTableDTO> patchDetails(@PathVariable UUID id, @RequestBody UpdateDecisionTableDetailsDTO decisionTable);

    @GetMapping(DECISION_TABLE_BY_GCE)
    ResponseEntity<DecisionTableDTO> findByGceId(@PathVariable UUID gceId);

    @GetMapping(DECISION_TABLE_PREVIEW)
    ResponseEntity<DecisionTableDTO> preview(@PathVariable UUID gceId);

    @PutMapping(DECISION_TABLE_REFRESH)
    ResponseEntity<DecisionTableDTO> refresh(@PathVariable UUID gceId);

    @GetMapping(DECISION_TABLE_PROJECT)
    ResponseEntity<List<DecisionTableDTO>> listByProject(@PathVariable UUID projectId);

    @DeleteMapping(ID)
    ResponseEntity<Map<String, Object>> delete(@PathVariable UUID id);

    @GetMapping(DECISION_TABLE_FUNCTIONAL_TEST_SIGNATURE)
    ResponseEntity<GenerateFunctionalTestSignatureResponseDTO> generateFunctionalTestSignature(@PathVariable UUID decisionTableId);
}
