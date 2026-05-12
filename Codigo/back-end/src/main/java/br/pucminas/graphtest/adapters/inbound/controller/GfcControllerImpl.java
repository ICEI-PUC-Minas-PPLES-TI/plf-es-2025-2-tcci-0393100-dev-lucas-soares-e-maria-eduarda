package br.pucminas.graphtest.adapters.inbound.controller;

import br.pucminas.graphtest.adapters.inbound.controller.interfaces.GfcController;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcSourceCodeDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcSourceMethodDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.PreviewGfcDTO;
import br.pucminas.graphtest.adapters.inbound.util.GfcDtoConverterUtil;
import br.pucminas.graphtest.application.exception.JavaSourceFileException;
import br.pucminas.graphtest.application.port.input.gfc.ListGfcSourceMethodsUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.PreviewGfcUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceMethodOutput;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static br.pucminas.graphtest.adapters.inbound.util.GfcDtoConverterUtil.toListMethodsInput;
import static br.pucminas.graphtest.adapters.inbound.util.GfcDtoConverterUtil.toPreviewInput;
import static br.pucminas.graphtest.adapters.inbound.util.GfcDtoConverterUtil.toDto;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.*;
import static br.pucminas.graphtest.shared.LogTopicsUtil.GFC_CONTROLLER;

@Slf4j(topic = GFC_CONTROLLER)
@RestController
@Validated
@RequestMapping(GFC)
@AllArgsConstructor
public class GfcControllerImpl implements GfcController {

    private final ListGfcSourceMethodsUseCasePort listGfcSourceMethodsUseCasePort;
    private final PreviewGfcUseCasePort previewGfcUseCasePort;

    @Override
    @PostMapping(GFC_SOURCE)
    public ResponseEntity<GfcSourceCodeDTO> source(@RequestParam("file") MultipartFile file) {
        log.info(">>> obterCodigoFonte: recebendo arquivo Java para obter codigo-fonte");

        String sourceCode = readJavaSourceFile(file);
        return ResponseEntity.ok(new GfcSourceCodeDTO(sourceCode));
    }

    @Override
    @PostMapping(GFC_SOURCE_METHODS)
    public ResponseEntity<List<GfcSourceMethodDTO>> listMethods(@RequestParam("file") MultipartFile file) {
        log.info(">>> listarMetodos: recebendo arquivo Java para listar metodos disponiveis");

        String sourceCode = readJavaSourceFile(file);
        List<GfcSourceMethodOutput> methods = listGfcSourceMethodsUseCasePort.execute(toListMethodsInput(sourceCode));
        return ResponseEntity.ok(methods.stream().map(GfcDtoConverterUtil::toDto).toList());
    }

    @Override
    @PostMapping(GFC_PREVIEW)
    public ResponseEntity<GfcDTO> preview(@Validated @RequestBody PreviewGfcDTO request) {
        log.info(">>> preVisualizar: recebendo requisicao para pre-visualizar GFC");

        GfcOutput graph = previewGfcUseCasePort.execute(toPreviewInput(request));
        return ResponseEntity.ok(toDto(graph));
    }

    private String readJavaSourceFile(MultipartFile file) {
        validateJavaSourceFile(file);
        try {
            return new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new JavaSourceFileException("Nao foi possivel ler o arquivo Java enviado.");
        }
    }

    private void validateJavaSourceFile(MultipartFile file) {
        if (file == null) {
            throw new JavaSourceFileException("O arquivo Java e obrigatorio.");
        }
        if (file.isEmpty()) {
            throw new JavaSourceFileException("O arquivo Java enviado esta vazio.");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".java")) {
            throw new JavaSourceFileException("O arquivo enviado deve possuir extensao .java.");
        }
    }
}
