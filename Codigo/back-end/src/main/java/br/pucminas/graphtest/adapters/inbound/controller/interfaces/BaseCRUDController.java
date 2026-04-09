package br.pucminas.graphtest.adapters.inbound.controller.interfaces;

import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface BaseCRUDController<I, O, U> {

    ResponseEntity<Map<String, Object>> create(I obj);

    ResponseEntity<O> findById(UUID id);

    ResponseEntity<List<O>> listAll();

    ResponseEntity<U> update(UUID id, I obj);

    ResponseEntity<Map<String, Object>> delete(UUID id);
}
