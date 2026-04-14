package br.pucminas.graphtest.adapters.inbound.controller;

import br.pucminas.graphtest.adapters.inbound.controller.interfaces.DecisionTableController;
import br.pucminas.graphtest.adapters.inbound.dto.decisiontable.DecisionTableDTO;
import br.pucminas.graphtest.application.port.input.decisiontable.DeleteDecisionTableByIdUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.FindDecisionTableByGceIdUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.FindDecisionTableByIdUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.GenerateDecisionTableUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.ListDecisionTablesByProjectUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.PreviewDecisionTableUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.RefreshDecisionTableUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableOutput;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
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
import static br.pucminas.graphtest.shared.LogTopicsUtil.TABELA_DECISAO_CONTROLLER;
import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.OK;

@Slf4j(topic = TABELA_DECISAO_CONTROLLER)
@RestController
@Validated
@RequestMapping(DECISION_TABLE)
@AllArgsConstructor
public class DecisionTableControllerImpl implements DecisionTableController {

    private final GenerateDecisionTableUseCasePort generateDecisionTableUseCasePort;
    private final FindDecisionTableByIdUseCasePort findDecisionTableByIdUseCasePort;
    private final FindDecisionTableByGceIdUseCasePort findDecisionTableByGceIdUseCasePort;
    private final ListDecisionTablesByProjectUseCasePort listDecisionTablesByProjectUseCasePort;
    private final PreviewDecisionTableUseCasePort previewDecisionTableUseCasePort;
    private final RefreshDecisionTableUseCasePort refreshDecisionTableUseCasePort;
    private final DeleteDecisionTableByIdUseCasePort deleteDecisionTableByIdUseCasePort;

    @Override
    public ResponseEntity<DecisionTableDTO> generate(@PathVariable UUID gceId) {
        log.info(">>> gerar: recebendo requisicao para gerar tabela de decisao a partir do GCE");

        DecisionTableOutput decisionTable = generateDecisionTableUseCasePort.execute(toGenerateInput(gceId));
        return ResponseEntity.created(URI.create(DECISION_TABLE + "/" + decisionTable.id()))
                .body(toDto(decisionTable));
    }

    @Override
    public ResponseEntity<DecisionTableDTO> findById(@PathVariable UUID id) {
        log.info(">>> encontrarPorId: recebendo requisicao para encontrar tabela de decisao por id");

        return ResponseEntity.ok(toDto(findDecisionTableByIdUseCasePort.execute(toByIdInput(id))));
    }

    @Override
    public ResponseEntity<DecisionTableDTO> findByGceId(@PathVariable UUID gceId) {
        log.info(">>> encontrarPorGceId: recebendo requisicao para encontrar tabela de decisao por GCE");

        return ResponseEntity.ok(toDto(findDecisionTableByGceIdUseCasePort.execute(toByGceIdInput(gceId))));
    }

    @Override
    public ResponseEntity<List<DecisionTableDTO>> listByProject(@PathVariable UUID projectId) {
        log.info(">>> listarPorProjeto: recebendo requisicao para listar tabelas de decisao por projeto");

        List<DecisionTableDTO> decisionTables = listDecisionTablesByProjectUseCasePort.execute(toListByProjectInput(projectId))
                .stream()
                .map(br.pucminas.graphtest.adapters.inbound.util.DecisionTableDtoConverterUtil::toDto)
                .toList();

        return ResponseEntity.ok(decisionTables);
    }

    @Override
    public ResponseEntity<DecisionTableDTO> preview(@PathVariable UUID gceId) {
        log.info(">>> preVisualizar: recebendo requisicao para derivar pre-visualizacao da tabela de decisao");

        return ResponseEntity.ok(toDto(previewDecisionTableUseCasePort.execute(toByGceIdInput(gceId))));
    }

    @Override
    public ResponseEntity<DecisionTableDTO> refresh(@PathVariable UUID gceId) {
        log.info(">>> atualizar: recebendo requisicao para regenerar tabela de decisao pelo GCE atual");

        return ResponseEntity.ok(toDto(refreshDecisionTableUseCasePort.execute(toByGceIdInput(gceId))));
    }

    @Override
    public ResponseEntity<Map<String, Object>> delete(@PathVariable UUID id) {
        log.info(">>> deletar: recebendo requisicao para deletar tabela de decisao");

        deleteDecisionTableByIdUseCasePort.execute(toByIdInput(id));

        return ResponseEntity.ok().body(buildJsonResponse(
                CHAVES_TABELA_DECISAO_CONTROLLER,
                asList(OK.value(), MSG_TABELA_DECISAO_DELETADA, id)
        ));
    }
}
