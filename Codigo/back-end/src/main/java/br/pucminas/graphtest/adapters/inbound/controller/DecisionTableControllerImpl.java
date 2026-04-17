package br.pucminas.graphtest.adapters.inbound.controller;

import br.pucminas.graphtest.adapters.inbound.controller.interfaces.DecisionTableController;
import br.pucminas.graphtest.adapters.inbound.controller.interfaces.OperacoesCRUDController;
import br.pucminas.graphtest.adapters.inbound.dto.decisiontable.DecisionTableDTO;
import br.pucminas.graphtest.adapters.inbound.dto.decisiontable.UpdateDecisionTableDetailsDTO;
import br.pucminas.graphtest.adapters.inbound.util.DecisionTableDtoConverterUtil;
import br.pucminas.graphtest.application.port.input.decisiontable.DeleteDecisionTableByIdUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.FindDecisionTableByGceIdUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.FindDecisionTableByIdUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.FindDecisionTableStatusByIdUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.GenerateDecisionTableUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.ListDecisionTablesUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.ListDecisionTablesByProjectUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.PatchDecisionTableDetailsUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.PreviewDecisionTableUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.RefreshDecisionTableUseCasePort;
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

import static br.pucminas.graphtest.adapters.inbound.util.ControllerConstantsUtil.CHAVES_TABELA_DECISAO_CONTROLLER;
import static br.pucminas.graphtest.adapters.inbound.util.ControllerConstantsUtil.MSG_TABELA_DECISAO_DELETADA;
import static br.pucminas.graphtest.adapters.inbound.util.DecisionTableDtoConverterUtil.toByGceIdInput;
import static br.pucminas.graphtest.adapters.inbound.util.DecisionTableDtoConverterUtil.toByIdInput;
import static br.pucminas.graphtest.adapters.inbound.util.DecisionTableDtoConverterUtil.toDto;
import static br.pucminas.graphtest.adapters.inbound.util.DecisionTableDtoConverterUtil.toGenerateInput;
import static br.pucminas.graphtest.adapters.inbound.util.DecisionTableDtoConverterUtil.toListByProjectInput;
import static br.pucminas.graphtest.adapters.inbound.util.JsonResponseBuilderUtil.buildJsonResponse;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.DECISION_TABLE;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.DECISION_TABLE_BY_GCE;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.DECISION_TABLE_GENERATE;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.DECISION_TABLE_PREVIEW;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.DECISION_TABLE_PROJECT;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.DECISION_TABLE_REFRESH;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.DECISION_TABLE_STATUS;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.ID;
import static br.pucminas.graphtest.shared.LogTopicsUtil.TABELA_DECISAO_CONTROLLER;
import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j(topic = TABELA_DECISAO_CONTROLLER)
@RestController
@Validated
@RequestMapping(DECISION_TABLE)
@AllArgsConstructor
public class DecisionTableControllerImpl implements DecisionTableController, OperacoesCRUDController<UUID, DecisionTableDTO> {

    private final ListDecisionTablesUseCasePort listDecisionTablesUseCasePort;
    private final GenerateDecisionTableUseCasePort generateDecisionTableUseCasePort;
    private final FindDecisionTableByIdUseCasePort findDecisionTableByIdUseCasePort;
    private final FindDecisionTableStatusByIdUseCasePort findDecisionTableStatusByIdUseCasePort;
    private final FindDecisionTableByGceIdUseCasePort findDecisionTableByGceIdUseCasePort;
    private final ListDecisionTablesByProjectUseCasePort listDecisionTablesByProjectUseCasePort;
    private final PatchDecisionTableDetailsUseCasePort patchDecisionTableDetailsUseCasePort;
    private final PreviewDecisionTableUseCasePort previewDecisionTableUseCasePort;
    private final RefreshDecisionTableUseCasePort refreshDecisionTableUseCasePort;
    private final DeleteDecisionTableByIdUseCasePort deleteDecisionTableByIdUseCasePort;

    @Override
    @PostMapping(DECISION_TABLE_GENERATE)
    public ResponseEntity<DecisionTableDTO> create(@PathVariable UUID gceId) {
        log.info(">>> criar: recebendo requisicao para criar tabela de decisao a partir do GCE");

        var decisionTable = generateDecisionTableUseCasePort.execute(toGenerateInput(gceId));

        return ResponseEntity.created(URI.create(DECISION_TABLE + "/" + decisionTable.id()))
                .body(toDto(decisionTable));
    }

    @Override
    @GetMapping
    public ResponseEntity<List<DecisionTableDTO>> listAll() {
        log.info(">>> listarTodos: recebendo requisicao para listar tabelas de decisao do usuario autenticado");

        List<DecisionTableDTO> decisionTables = listDecisionTablesUseCasePort.execute().stream()
                .map(DecisionTableDtoConverterUtil::toDto)
                .toList();

        return ResponseEntity.ok(decisionTables);
    }

    @Override
    @GetMapping(ID)
    public ResponseEntity<DecisionTableDTO> findById(@PathVariable UUID id) {
        log.info(">>> encontrarPorId: recebendo requisicao para encontrar tabela de decisao por id");

        return ResponseEntity.ok(toDto(findDecisionTableByIdUseCasePort.execute(toByIdInput(id))));
    }

    @Override
    @GetMapping(DECISION_TABLE_STATUS)
    public ResponseEntity<Boolean> findStatusById(@PathVariable UUID id) {
        log.info(">>> encontrarStatusPorId: recebendo requisicao para consultar status da tabela de decisao");

        return ResponseEntity.ok(findDecisionTableStatusByIdUseCasePort.execute(toByIdInput(id)));
    }

    @Override
    @PatchMapping(ID)
    public ResponseEntity<DecisionTableDTO> patchDetails(@PathVariable UUID id,
                                                         @RequestBody UpdateDecisionTableDetailsDTO decisionTable) {
        log.info(">>> atualizarDetalhes: recebendo requisicao para atualizar nome e descricao da tabela de decisao");

        return ResponseEntity.ok(
                toDto(patchDecisionTableDetailsUseCasePort.execute(
                        DecisionTableDtoConverterUtil.toUpdateDetailsInput(id, decisionTable)
                ))
        );
    }

    @Override
    @GetMapping(DECISION_TABLE_BY_GCE)
    public ResponseEntity<DecisionTableDTO> findByGceId(@PathVariable UUID gceId) {
        log.info(">>> encontrarPorGceId: recebendo requisicao para encontrar tabela de decisao por GCE");

        return ResponseEntity.ok(toDto(findDecisionTableByGceIdUseCasePort.execute(toByGceIdInput(gceId))));
    }

    @Override
    @GetMapping(DECISION_TABLE_PREVIEW)
    public ResponseEntity<DecisionTableDTO> preview(@PathVariable UUID gceId) {
        log.info(">>> preVisualizar: recebendo requisicao para pre-visualizar tabela de decisao a partir do GCE");

        return ResponseEntity.ok(toDto(previewDecisionTableUseCasePort.execute(toByGceIdInput(gceId))));
    }

    @Override
    @PutMapping(DECISION_TABLE_REFRESH)
    public ResponseEntity<DecisionTableDTO> refresh(@PathVariable UUID gceId) {
        log.info(">>> sincronizar: recebendo requisicao para sincronizar tabela de decisao com o GCE");

        return ResponseEntity.ok(toDto(refreshDecisionTableUseCasePort.execute(toByGceIdInput(gceId))));
    }

    @Override
    @GetMapping(DECISION_TABLE_PROJECT)
    public ResponseEntity<List<DecisionTableDTO>> listByProject(@PathVariable UUID projectId) {
        log.info(">>> listarPorProjeto: recebendo requisicao para listar tabelas de decisao por projeto");

        List<DecisionTableDTO> decisionTables = listDecisionTablesByProjectUseCasePort.execute(toListByProjectInput(projectId))
                .stream()
                .map(DecisionTableDtoConverterUtil::toDto)
                .toList();

        return ResponseEntity.ok(decisionTables);
    }

    @Override
    @DeleteMapping(ID)
    public ResponseEntity<Map<String, Object>> delete(@PathVariable UUID id) {
        log.info(">>> deletar: recebendo requisicao para deletar tabela de decisao");

        deleteDecisionTableByIdUseCasePort.execute(toByIdInput(id));

        return ResponseEntity.ok().body(buildJsonResponse(
                CHAVES_TABELA_DECISAO_CONTROLLER,
                asList(OK.value(), MSG_TABELA_DECISAO_DELETADA, id)
        ));
    }
}
