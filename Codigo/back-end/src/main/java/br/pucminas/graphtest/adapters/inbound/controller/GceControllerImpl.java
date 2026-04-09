package br.pucminas.graphtest.adapters.inbound.controller;

import br.pucminas.graphtest.adapters.inbound.controller.interfaces.GceController;
import br.pucminas.graphtest.adapters.inbound.dto.gce.AddGceNodeDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gce.GceDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gce.GceInputDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gce.UpdateGceNodeDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gce.ValidationGceDTO;
import br.pucminas.graphtest.adapters.inbound.util.GceDtoConverterUtil;
import br.pucminas.graphtest.application.port.input.gce.AddNodeToGceUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.CreateGceUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.DeleteGceUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.FindGceByIdUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.ListGcesUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.ListGcesByProjectUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.ToggleGceEdgeUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.UpdateGceNodeUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.UpdateGceUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.ValidateGceUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.records.FindGceByIdInput;
import br.pucminas.graphtest.application.port.input.gce.records.GceOutput;
import br.pucminas.graphtest.application.port.input.gce.records.DeleteGceInput;
import br.pucminas.graphtest.application.port.input.gce.records.ListGcesByProjectInput;
import br.pucminas.graphtest.application.port.input.gce.records.ValidationGceOutput;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static br.pucminas.graphtest.adapters.inbound.util.ControllerConstantsUtil.CHAVES_GCE_CONTROLLER;
import static br.pucminas.graphtest.adapters.inbound.util.ControllerConstantsUtil.MSG_GCE_DELETADO;
import static br.pucminas.graphtest.adapters.inbound.util.GceDtoConverterUtil.toCreateInput;
import static br.pucminas.graphtest.adapters.inbound.util.GceDtoConverterUtil.toAddNodeInput;
import static br.pucminas.graphtest.adapters.inbound.util.ControllerConstantsUtil.MSG_GCE_CRIADO;
import static br.pucminas.graphtest.adapters.inbound.util.GceDtoConverterUtil.toDto;
import static br.pucminas.graphtest.adapters.inbound.util.GceDtoConverterUtil.toToggleEdgeInput;
import static br.pucminas.graphtest.adapters.inbound.util.GceDtoConverterUtil.toUpdateInput;
import static br.pucminas.graphtest.adapters.inbound.util.GceDtoConverterUtil.toUpdateNodeInput;
import static br.pucminas.graphtest.adapters.inbound.util.GceDtoConverterUtil.toValidateInput;
import static br.pucminas.graphtest.adapters.inbound.util.JsonResponseBuilderUtil.buildJsonResponse;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.GCE;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.GCE_EDGE_TOGGLE;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.GCE_NODE;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.GCE_NODES;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.GCE_PROJETO;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.GCE_VALIDAR;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.ID;
import static br.pucminas.graphtest.shared.LogTopicsUtil.GCE_CONTROLLER;
import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

/**
 * Controller HTTP responsavel pelos endpoints iniciais de GCE.
 */
@Slf4j(topic = GCE_CONTROLLER)
@RestController
@Validated
@RequestMapping(GCE)
@AllArgsConstructor
public class GceControllerImpl implements GceController {

    private final CreateGceUseCasePort createGceUseCasePort;
    private final DeleteGceUseCasePort deleteGceUseCasePort;
    private final FindGceByIdUseCasePort findGceByIdUseCasePort;
    private final ListGcesUseCasePort listGcesUseCasePort;
    private final ListGcesByProjectUseCasePort listGcesByProjectUseCasePort;
    private final ValidateGceUseCasePort validateGceUseCasePort;
    private final UpdateGceUseCasePort updateGceUseCasePort;
    private final AddNodeToGceUseCasePort addNodeToGceUseCasePort;
    private final UpdateGceNodeUseCasePort updateGceNodeUseCasePort;
    private final ToggleGceEdgeUseCasePort toggleGceEdgeUseCasePort;

    @Override
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Validated @RequestBody GceInputDTO graph) {
        log.info(">>> criar: recebendo requisicao para criar GCE");

        GceOutput graphCreated = createGceUseCasePort.execute(toCreateInput(graph));

        return ResponseEntity.created(URI.create(GCE + "/" + graphCreated.id()))
                .body(buildJsonResponse(
                        CHAVES_GCE_CONTROLLER,
                        asList(CREATED.value(), MSG_GCE_CRIADO, graphCreated.id())
                ));
    }

    @Override
    @GetMapping(ID)
    public ResponseEntity<GceDTO> findById(@PathVariable UUID id) {
        log.info(">>> encontrarPorId: recebendo requisicao para encontrar GCE por id");

        GceOutput graph = findGceByIdUseCasePort.execute(new FindGceByIdInput(id));
        return ResponseEntity.ok(toDto(graph));
    }

    @Override
    @GetMapping
    public ResponseEntity<List<GceDTO>> listAll() {
        log.info(">>> listarTodos: recebendo requisicao para listar GCEs do usuario autenticado");

        List<GceOutput> graphs = listGcesUseCasePort.execute();
        return ResponseEntity.ok(graphs.stream().map(GceDtoConverterUtil::toDto).toList());
    }

    @Override
    @GetMapping(GCE_PROJETO)
    public ResponseEntity<List<GceDTO>> listByProject(@PathVariable UUID projectId) {
        log.info(">>> listarPorProjeto: recebendo requisicao para listar GCEs por projeto");

        List<GceOutput> graphs = listGcesByProjectUseCasePort.execute(new ListGcesByProjectInput(projectId));
        return ResponseEntity.ok(graphs.stream().map(GceDtoConverterUtil::toDto).toList());
    }

    @Override
    @DeleteMapping(ID)
    public ResponseEntity<Map<String, Object>> delete(@PathVariable UUID id) {
        log.info(">>> deletar: recebendo requisicao para deletar GCE");

        deleteGceUseCasePort.execute(new DeleteGceInput(id));

        return ResponseEntity.ok().body(buildJsonResponse(
                CHAVES_GCE_CONTROLLER,
                asList(OK.value(), MSG_GCE_DELETADO, id)
        ));
    }

    @Override
    @PutMapping(ID)
    public ResponseEntity<GceDTO> update(@PathVariable UUID id, @RequestBody GceInputDTO graph) {
        log.info(">>> atualizar: recebendo requisicao para substituir a representacao completa do GCE");

        GceOutput updatedGraph = updateGceUseCasePort.execute(toUpdateInput(id, graph));
        return ResponseEntity.ok(toDto(updatedGraph));
    }

    @Override
    @PostMapping(GCE_VALIDAR)
    public ResponseEntity<ValidationGceDTO> validate(@RequestBody GceInputDTO graph) {
        log.info(">>> validar: recebendo requisicao para validar GCE");

        ValidationGceOutput validation = validateGceUseCasePort.execute(toValidateInput(graph));
        return ResponseEntity.ok(toDto(validation));
    }

    @Override
    @PostMapping(GCE_NODES)
    public ResponseEntity<GceDTO> addNode(@PathVariable UUID id, @RequestBody AddGceNodeDTO node) {
        log.info(">>> adicionarNo: recebendo requisicao para adicionar no ao GCE");

        GceOutput updatedGraph = addNodeToGceUseCasePort.execute(toAddNodeInput(id, node));
        return ResponseEntity.ok(toDto(updatedGraph));
    }

    @Override
    @PatchMapping(GCE_NODE)
    public ResponseEntity<GceDTO> updateNode(@PathVariable UUID id,
                                             @PathVariable String nodeCode,
                                             @RequestBody UpdateGceNodeDTO node) {
        log.info(">>> atualizarNo: recebendo requisicao para atualizar no do GCE");

        GceOutput updatedGraph = updateGceNodeUseCasePort.execute(toUpdateNodeInput(id, nodeCode, node));
        return ResponseEntity.ok(toDto(updatedGraph));
    }

    @Override
    @PatchMapping(GCE_EDGE_TOGGLE)
    public ResponseEntity<GceDTO> toggleEdge(@PathVariable UUID id,
                                             @PathVariable UUID edgeId) {
        log.info(">>> inverterAresta: recebendo requisicao para inverter aresta do GCE");

        GceOutput updatedGraph = toggleGceEdgeUseCasePort.execute(toToggleEdgeInput(id, edgeId));
        return ResponseEntity.ok(toDto(updatedGraph));
    }
}
