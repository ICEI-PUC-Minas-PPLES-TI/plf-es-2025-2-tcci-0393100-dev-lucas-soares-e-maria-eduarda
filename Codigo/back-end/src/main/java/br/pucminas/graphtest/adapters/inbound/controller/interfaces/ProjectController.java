package br.pucminas.graphtest.adapters.inbound.controller.interfaces;

import br.pucminas.graphtest.adapters.inbound.dto.project.ProjectDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.ID;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.PROJETO_MEUS;

public interface ProjectController {

    @PutMapping(ID)
    ResponseEntity<Map<String, Object>> update(
            @PathVariable java.util.UUID id,
            @Validated(ProjectDTO.Update.class) @RequestBody ProjectDTO projeto
    );

    @GetMapping(PROJETO_MEUS)
    ResponseEntity<List<ProjectDTO>> listMine();
}
