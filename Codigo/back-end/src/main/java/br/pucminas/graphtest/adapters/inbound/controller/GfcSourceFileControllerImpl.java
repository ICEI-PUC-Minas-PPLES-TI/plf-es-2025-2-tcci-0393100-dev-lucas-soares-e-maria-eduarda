package br.pucminas.graphtest.adapters.inbound.controller;

import br.pucminas.graphtest.adapters.inbound.controller.interfaces.GfcSourceFileController;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.CreateGfcSourceFileResponseDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.DeleteGfcSourceFileResponseDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcSourceCodeDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcSourceFileDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcSourceMethodDetailsDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcSourceMethodDTO;
import br.pucminas.graphtest.adapters.inbound.util.GfcDtoConverterUtil;
import br.pucminas.graphtest.application.port.input.gfc.CreateGfcSourceFileUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.DeleteGfcSourceFileUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.FindGfcSourceFileByIdUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.GetGfcSourceCodeUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.GetGfcSourceMethodDetailsUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.ListGfcSourceFilesByProjectUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.ListGfcSourceMethodsUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.records.CreateGfcSourceFileOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceCodeOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceFileOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceMethodDetailsOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceMethodOutput;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static br.pucminas.graphtest.adapters.inbound.util.GfcDtoConverterUtil.toCreateSourceFileInput;
import static br.pucminas.graphtest.adapters.inbound.util.GfcDtoConverterUtil.toDto;
import static br.pucminas.graphtest.adapters.inbound.util.GfcJavaSourceFileUploadUtil.readJavaSourceFile;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.GFC;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.GFC_SOURCE_FILE;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.GFC_SOURCE_FILE_ID;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.GFC_SOURCE_FILE_METHOD_DETAILS;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.GFC_SOURCE_FILE_METHODS;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.GFC_SOURCE_FILE_PROJECT;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.GFC_SOURCE_FILE_SOURCE_CODE;
import static br.pucminas.graphtest.shared.LogTopicsUtil.GFC_CONTROLLER;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j(topic = GFC_CONTROLLER)
@RestController
@Validated
@RequestMapping(GFC)
@AllArgsConstructor
public class GfcSourceFileControllerImpl implements GfcSourceFileController {

    private static final String MSG_GFC_SOURCE_FILE_REMOVIDO = "Arquivo-fonte removido com sucesso";

    private final CreateGfcSourceFileUseCasePort createGfcSourceFileUseCasePort;
    private final FindGfcSourceFileByIdUseCasePort findGfcSourceFileByIdUseCasePort;
    private final ListGfcSourceFilesByProjectUseCasePort listGfcSourceFilesByProjectUseCasePort;
    private final GetGfcSourceCodeUseCasePort getGfcSourceCodeUseCasePort;
    private final ListGfcSourceMethodsUseCasePort listGfcSourceMethodsUseCasePort;
    private final GetGfcSourceMethodDetailsUseCasePort getGfcSourceMethodDetailsUseCasePort;
    private final DeleteGfcSourceFileUseCasePort deleteGfcSourceFileUseCasePort;

    @Override
    @PostMapping(GFC_SOURCE_FILE)
    public ResponseEntity<CreateGfcSourceFileResponseDTO> createSourceFile(@RequestParam("projectId") UUID projectId,
                                                                           @RequestParam("file") MultipartFile file) {
        log.info(">>> cadastrarArquivoFonte: recebendo arquivo Java para cadastrar na feature GFC");

        String sourceCode = readJavaSourceFile(file);
        CreateGfcSourceFileOutput output = createGfcSourceFileUseCasePort.execute(
                toCreateSourceFileInput(projectId, file.getOriginalFilename(), sourceCode)
        );

        return ResponseEntity.created(URI.create(GFC + GFC_SOURCE_FILE + "/" + output.sourceFileId()))
                .body(toDto(output, CREATED.value()));
    }

    @Override
    @GetMapping(GFC_SOURCE_FILE_ID)
    public ResponseEntity<GfcSourceFileDTO> findById(@PathVariable UUID sourceFileId) {
        log.info(">>> buscarArquivoFonte: recebendo requisicao para buscar metadados de source-file GFC");

        GfcSourceFileOutput output = findGfcSourceFileByIdUseCasePort.execute(sourceFileId);
        return ResponseEntity.ok(toDto(output));
    }

    @Override
    @GetMapping(GFC_SOURCE_FILE_PROJECT)
    public ResponseEntity<List<GfcSourceFileDTO>> listByProject(@PathVariable UUID projectId) {
        log.info(">>> listarArquivosFontePorProjeto: recebendo requisicao para listar source-files GFC por projeto");

        List<GfcSourceFileOutput> outputs = listGfcSourceFilesByProjectUseCasePort.execute(projectId);
        return ResponseEntity.ok(outputs.stream().map(GfcDtoConverterUtil::toDto).toList());
    }

    @Override
    @GetMapping(GFC_SOURCE_FILE_SOURCE_CODE)
    public ResponseEntity<GfcSourceCodeDTO> getSourceCode(@PathVariable UUID sourceFileId) {
        log.info(">>> obterCodigoFonte: recebendo requisicao para obter codigo-fonte de source-file GFC");

        GfcSourceCodeOutput output = getGfcSourceCodeUseCasePort.execute(sourceFileId);
        return ResponseEntity.ok(toDto(output));
    }

    @Override
    @GetMapping(GFC_SOURCE_FILE_METHODS)
    public ResponseEntity<List<GfcSourceMethodDTO>> listMethods(@PathVariable UUID sourceFileId) {
        log.info(">>> listarMetodos: recebendo requisicao para listar metodos de source-file GFC");

        List<GfcSourceMethodOutput> methods = listGfcSourceMethodsUseCasePort.execute(sourceFileId);
        return ResponseEntity.ok(methods.stream().map(GfcDtoConverterUtil::toDto).toList());
    }

    @Override
    @GetMapping(GFC_SOURCE_FILE_METHOD_DETAILS)
    public ResponseEntity<GfcSourceMethodDetailsDTO> getMethodDetails(@PathVariable UUID sourceFileId,
                                                                      @RequestParam("signature") String methodSignature) {
        log.info(">>> detalharMetodo: recebendo requisicao para detalhar metodo de source-file GFC");

        GfcSourceMethodDetailsOutput output = getGfcSourceMethodDetailsUseCasePort.execute(sourceFileId, methodSignature);
        return ResponseEntity.ok(toDto(output));
    }

    @Override
    @DeleteMapping(GFC_SOURCE_FILE_ID)
    public ResponseEntity<DeleteGfcSourceFileResponseDTO> delete(@PathVariable UUID sourceFileId) {
        log.info(">>> removerArquivoFonte: recebendo requisicao para remover source-file GFC");

        deleteGfcSourceFileUseCasePort.execute(sourceFileId);
        return ResponseEntity.ok(new DeleteGfcSourceFileResponseDTO(MSG_GFC_SOURCE_FILE_REMOVIDO, OK.value()));
    }
}
