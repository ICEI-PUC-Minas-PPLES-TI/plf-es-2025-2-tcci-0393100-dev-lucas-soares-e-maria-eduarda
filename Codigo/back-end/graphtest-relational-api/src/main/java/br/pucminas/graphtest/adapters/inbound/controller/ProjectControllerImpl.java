package br.pucminas.graphtest.adapters.inbound.controller;

import br.pucminas.graphtest.adapters.inbound.controller.interfaces.ProjectController;
import br.pucminas.graphtest.adapters.inbound.dto.ProjectDTO;
import br.pucminas.graphtest.adapters.inbound.util.EntityDtoConverterUtil;
import br.pucminas.graphtest.application.port.input.project.CreateProjectUseCase;
import br.pucminas.graphtest.application.port.input.project.DeleteProjectUseCase;
import br.pucminas.graphtest.application.port.input.project.FindProjectByIdUseCase;
import br.pucminas.graphtest.application.port.input.project.ListProjectsByUserUseCase;
import br.pucminas.graphtest.application.port.input.project.ListProjectsUseCase;
import br.pucminas.graphtest.application.port.input.project.UpdateProjectUseCase;
import br.pucminas.graphtest.application.port.input.project.records.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static br.pucminas.graphtest.adapters.inbound.util.ControllerConstantsUtil.CHAVES_PROJETO_CONTROLLER;
import static br.pucminas.graphtest.adapters.inbound.util.ControllerConstantsUtil.ENDPOINT_PROJETO;
import static br.pucminas.graphtest.adapters.inbound.util.ControllerConstantsUtil.MSG_PROJETO_ATUALIZADO;
import static br.pucminas.graphtest.adapters.inbound.util.ControllerConstantsUtil.MSG_PROJETO_CRIADO;
import static br.pucminas.graphtest.adapters.inbound.util.ControllerConstantsUtil.MSG_PROJETO_DELETADO;
import static br.pucminas.graphtest.adapters.inbound.util.EntityDtoConverterUtil.toDto;
import static br.pucminas.graphtest.adapters.inbound.util.JsonResponseBuilderUtil.buildJsonResponse;
import static br.pucminas.graphtest.shared.LogTopicsUtil.PROJETO_CONTROLLER;
import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j(topic = PROJETO_CONTROLLER)
@RestController
@Validated
@RequestMapping(ENDPOINT_PROJETO)
@AllArgsConstructor
public class ProjectControllerImpl implements ProjectController {

    private final CreateProjectUseCase createProjectUseCase;
    private final DeleteProjectUseCase deleteProjectUseCase;
    private final FindProjectByIdUseCase findProjectByIdUseCase;
    private final ListProjectsUseCase listProjectsUseCase;
    private final ListProjectsByUserUseCase listProjectsByUserUseCase;
    private final UpdateProjectUseCase updateProjectUseCase;

    @Override
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(
            @Validated(ProjectDTO.Create.class) @RequestBody ProjectDTO projeto
    ) {
        log.info(">>> criar: recebendo requisicao para criar projeto");

        ProjectOutput projectCreated = createProjectUseCase.execute(
                new CreateProjectInput(projeto.name(), projeto.description()));

        return ResponseEntity.created(URI.create("/projeto/" + projectCreated.id()))
                .body(buildJsonResponse(
                        CHAVES_PROJETO_CONTROLLER,
                        asList(CREATED.value(), MSG_PROJETO_CRIADO, projectCreated.id())
                ));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> findById(@PathVariable UUID id) {
        log.info(">>> encontrarPorId: recebendo requisicao para encontrar projeto por id");

        ProjectOutput project = findProjectByIdUseCase.execute(new FindProjectByIdInput(id));

        return ResponseEntity.ok().body(toDto(project));
    }

    @Override
    @GetMapping
    public ResponseEntity<List<ProjectDTO>> listAll() {
        log.info(">>> listarTodos: recebendo requisicao para listar todos projetos");

        List<ProjectOutput> projects = listProjectsUseCase.execute();

        return ResponseEntity.ok()
                .body(projects.stream()
                        .map(EntityDtoConverterUtil::toDto)
                        .toList());
    }

    @Override
    @GetMapping("/meus")
    public ResponseEntity<List<ProjectDTO>> listMine() {
        log.info(">>> listarMeus: recebendo requisicao para listar projetos do usuario autenticado");

        List<ProjectOutput> projects = listProjectsByUserUseCase.execute();

        return ResponseEntity.ok()
                .body(projects.stream()
                        .map(EntityDtoConverterUtil::toDto)
                        .toList());
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(
            @PathVariable UUID id,
            @Validated(ProjectDTO.Update.class) @RequestBody @NotNull ProjectDTO projeto
    ) {
        log.info(">>> atualizar: recebendo requisicao para atualizar projeto");

        ProjectOutput projectUpdated = updateProjectUseCase.execute(
                new UpdateProjectInput(id, projeto.name(), projeto.description())
        );

        return ResponseEntity.ok()
                .body(buildJsonResponse(
                        CHAVES_PROJETO_CONTROLLER,
                        asList(OK.value(), MSG_PROJETO_ATUALIZADO, projectUpdated.id())
                ));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable UUID id) {
        log.info(">>> deletar: recebendo requisicao para deletar projeto");

        deleteProjectUseCase.execute(new DeleteProjectInput(id));

        return ResponseEntity.ok()
                .body(buildJsonResponse(
                        CHAVES_PROJETO_CONTROLLER,
                        asList(OK.value(), MSG_PROJETO_DELETADO, id)
                ));
    }
}
