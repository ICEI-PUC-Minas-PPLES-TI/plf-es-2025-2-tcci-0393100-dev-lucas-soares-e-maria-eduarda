package br.pucminas.graphtest.adapters.inbound.controller.interfaces;

import br.pucminas.graphtest.adapters.inbound.dto.project.ProjectDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.PROJETO_MEUS;

public interface ProjectController extends BaseCRUDController<ProjectDTO, ProjectDTO, Map<String, Object>> {

    @GetMapping(PROJETO_MEUS)
    ResponseEntity<List<ProjectDTO>> listMine();
}
