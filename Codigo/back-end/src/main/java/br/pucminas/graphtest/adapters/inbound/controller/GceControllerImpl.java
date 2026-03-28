package br.pucminas.graphtest.adapters.inbound.controller;

import br.pucminas.graphtest.adapters.inbound.controller.interfaces.GceController;
import br.pucminas.graphtest.adapters.inbound.dto.GceDTO;
import br.pucminas.graphtest.adapters.inbound.dto.GceInputDTO;
import br.pucminas.graphtest.adapters.inbound.dto.ValidationGceDTO;
import br.pucminas.graphtest.application.port.input.gce.CreateGceUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.FindGceByIdUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.ValidateGceUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.records.FindGceByIdInput;
import br.pucminas.graphtest.application.port.input.gce.records.GceOutput;
import br.pucminas.graphtest.application.port.input.gce.records.ValidationGceOutput;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

import static br.pucminas.graphtest.adapters.inbound.util.ControllerConstantsUtil.CHAVES_GCE_CONTROLLER;
import static br.pucminas.graphtest.adapters.inbound.util.GceDtoConverterUtil.toCreateInput;
import static br.pucminas.graphtest.adapters.inbound.util.GceDtoConverterUtil.toDomain;
import static br.pucminas.graphtest.adapters.inbound.util.ControllerConstantsUtil.MSG_GCE_CRIADO;
import static br.pucminas.graphtest.adapters.inbound.util.GceDtoConverterUtil.toDto;
import static br.pucminas.graphtest.adapters.inbound.util.JsonResponseBuilderUtil.buildJsonResponse;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.GCE;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.GCE_VALIDAR;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.ID;
import static br.pucminas.graphtest.shared.LogTopicsUtil.GCE_CONTROLLER;
import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.CREATED;

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
    private final FindGceByIdUseCasePort findGceByIdUseCasePort;
    private final ValidateGceUseCasePort validateGceUseCasePort;

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
    @PostMapping(GCE_VALIDAR)
    public ResponseEntity<ValidationGceDTO> validate(@Validated @RequestBody GceInputDTO graph) {
        log.info(">>> validar: recebendo requisicao para validar GCE");

        ValidationGceOutput validation = validateGceUseCasePort.execute(toDomain(graph));
        return ResponseEntity.ok(toDto(validation));
    }
}
