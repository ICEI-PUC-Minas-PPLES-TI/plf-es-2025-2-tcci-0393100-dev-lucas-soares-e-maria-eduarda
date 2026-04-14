package br.pucminas.graphtest.adapters.inbound.controller.interfaces;

import br.pucminas.graphtest.adapters.inbound.dto.decisiontable.DecisionTableDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.DECISION_TABLE_GCE;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.DECISION_TABLE_GCE_PREVIEW;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.DECISION_TABLE_PROJECT;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.ID;

public interface DecisionTableController {

    @PostMapping(DECISION_TABLE_GCE)
    ResponseEntity<DecisionTableDTO> generate(@PathVariable UUID gceId);

    @GetMapping(ID)
    ResponseEntity<DecisionTableDTO> findById(@PathVariable UUID id);

    @GetMapping(DECISION_TABLE_GCE)
    ResponseEntity<DecisionTableDTO> findByGceId(@PathVariable UUID gceId);

    @GetMapping(DECISION_TABLE_PROJECT)
    ResponseEntity<List<DecisionTableDTO>> listByProject(@PathVariable UUID projectId);

    @GetMapping(DECISION_TABLE_GCE_PREVIEW)
    ResponseEntity<DecisionTableDTO> preview(@PathVariable UUID gceId);

    @PutMapping(DECISION_TABLE_GCE)
    ResponseEntity<DecisionTableDTO> refresh(@PathVariable UUID gceId);

    @DeleteMapping(ID)
    ResponseEntity<Map<String, Object>> delete(@PathVariable UUID id);
}
