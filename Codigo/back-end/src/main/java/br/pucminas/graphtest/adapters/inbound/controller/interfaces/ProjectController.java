package br.pucminas.graphtest.adapters.inbound.controller.interfaces;

import br.pucminas.graphtest.adapters.inbound.dto.ProjectDTO;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.ID;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.PROJETO_MEUS;

public interface ProjectController extends BaseCRUDController<ProjectDTO> {

    @PostMapping
    ResponseEntity<Map<String, Object>> create(
            @Validated(ProjectDTO.Create.class) @RequestBody ProjectDTO obj
    );

    @PutMapping(ID)
    ResponseEntity<Map<String, Object>> update(
            @PathVariable UUID id,
            @Validated(ProjectDTO.Update.class) @RequestBody @NotNull ProjectDTO obj
    );

    @GetMapping(PROJETO_MEUS)
    ResponseEntity<List<ProjectDTO>> listMine();
}
