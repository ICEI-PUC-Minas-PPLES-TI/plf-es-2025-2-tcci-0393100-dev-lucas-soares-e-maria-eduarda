package br.pucminas.graphtest.adapters.inbound.controller.interfaces;

import br.pucminas.graphtest.adapters.inbound.dto.gce.AddGceNodeDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gce.GceDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gce.GceInputDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gce.UpdateGceDetailsDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gce.UpdateGceNodeDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gce.ValidationGceDTO;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.GCE_EDGE_TOGGLE;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.GCE_NODE;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.GCE_NODES;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.GCE_PROJETO;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.GCE_VALIDAR;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.ID;

public interface GceController {

    @PutMapping(ID)
    ResponseEntity<GceDTO> update(@PathVariable UUID id, @Validated @RequestBody GceInputDTO graph);

    @GetMapping(GCE_PROJETO)
    ResponseEntity<List<GceDTO>> listByProject(@PathVariable UUID projectId);

    @PostMapping(GCE_VALIDAR)
    ResponseEntity<ValidationGceDTO> validate(@RequestBody GceInputDTO graph);

    @PatchMapping("/{id}")
    ResponseEntity<GceDTO> patchDetails(@PathVariable UUID id, @RequestBody UpdateGceDetailsDTO graph);

    @PostMapping(GCE_NODES)
    ResponseEntity<GceDTO> addNode(@PathVariable UUID id, @RequestBody AddGceNodeDTO node);

    @PatchMapping(GCE_NODE)
    ResponseEntity<GceDTO> updateNode(@PathVariable UUID id,
                                      @PathVariable String nodeCode,
                                      @RequestBody UpdateGceNodeDTO node);

    @PatchMapping(GCE_EDGE_TOGGLE)
    ResponseEntity<GceDTO> toggleEdge(@PathVariable UUID id,
                                      @PathVariable UUID edgeId);
}
