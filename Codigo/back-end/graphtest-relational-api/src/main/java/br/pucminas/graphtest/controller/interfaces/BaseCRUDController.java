package br.pucminas.graphtest.controller.interfaces;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface BaseCRUDController<O> {

    @PostMapping
    ResponseEntity<Map<String, Object>> criar(@Valid @RequestBody O obj);

    @GetMapping("/{id}")
    ResponseEntity<O> encontrarPorId(@PathVariable UUID id);

    @GetMapping
    ResponseEntity<List<O>> listarTodos();

    @PutMapping("/{id}")
    ResponseEntity<Map<String, Object>> atualizar(@PathVariable UUID id, @Valid @RequestBody O obj);


    @DeleteMapping("/{id}")
    ResponseEntity<Map<String, Object>> deletar(@PathVariable UUID id);

}
