package br.pucminas.graphtest.adapters.inbound.controller.interfaces;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface BaseCRUDController<O> {

    @GetMapping("/{id}")
    ResponseEntity<O> findById(@PathVariable UUID id);

    @GetMapping
    ResponseEntity<List<O>> listAll();


    @DeleteMapping("/{id}")
    ResponseEntity<Map<String, Object>> delete(@PathVariable UUID id);

}
