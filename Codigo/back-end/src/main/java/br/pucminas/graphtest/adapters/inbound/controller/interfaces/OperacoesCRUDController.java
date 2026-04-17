package br.pucminas.graphtest.adapters.inbound.controller.interfaces;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.ID;

@Validated
public interface OperacoesCRUDController<I, O> {

    @PostMapping
    ResponseEntity<?> create(I input);

    @GetMapping(ID)
    ResponseEntity<O> findById(UUID id);

    @GetMapping
    ResponseEntity<List<O>> listAll();

    @DeleteMapping(ID)
    ResponseEntity<Map<String, Object>> delete(UUID id);
}
