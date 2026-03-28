package br.pucminas.graphtest.adapters.inbound.controller.interfaces;

import br.pucminas.graphtest.adapters.inbound.dto.GceDTO;
import br.pucminas.graphtest.adapters.inbound.dto.GceInputDTO;
import br.pucminas.graphtest.adapters.inbound.dto.ValidationGceDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.GCE_VALIDAR;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.ID;

/**
 * Contrato HTTP do controller de GCE.
 */
public interface GceController {

    @PostMapping
    ResponseEntity<Map<String, Object>> create(@Validated @RequestBody GceInputDTO graph);

    @GetMapping(ID)
    ResponseEntity<GceDTO> findById(@PathVariable Long id);

    @PostMapping(GCE_VALIDAR)
    ResponseEntity<ValidationGceDTO> validate(@Validated @RequestBody GceInputDTO graph);
}
