package br.pucminas.graphtest.adapters.inbound.controller.interfaces;

import br.pucminas.graphtest.adapters.inbound.dto.gfc.CreateGfcSourceFileResponseDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.DeleteGfcSourceFileResponseDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcSourceCodeDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcSourceFileDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcSourceMethodDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.GFC_SOURCE_FILE;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.GFC_SOURCE_FILE_ID;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.GFC_SOURCE_FILE_METHODS;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.GFC_SOURCE_FILE_PROJECT;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.GFC_SOURCE_FILE_SOURCE_CODE;

public interface GfcSourceFileController {

    @PostMapping(GFC_SOURCE_FILE)
    ResponseEntity<CreateGfcSourceFileResponseDTO> createSourceFile(@RequestParam("projectId") UUID projectId,
                                                                    @RequestParam("file") MultipartFile file);

    @GetMapping(GFC_SOURCE_FILE_ID)
    ResponseEntity<GfcSourceFileDTO> findById(@PathVariable UUID sourceFileId);

    @GetMapping(GFC_SOURCE_FILE_PROJECT)
    ResponseEntity<List<GfcSourceFileDTO>> listByProject(@PathVariable UUID projectId);

    @GetMapping(GFC_SOURCE_FILE_SOURCE_CODE)
    ResponseEntity<GfcSourceCodeDTO> getSourceCode(@PathVariable UUID sourceFileId);

    @GetMapping(GFC_SOURCE_FILE_METHODS)
    ResponseEntity<List<GfcSourceMethodDTO>> listMethods(@PathVariable UUID sourceFileId);

    @DeleteMapping(GFC_SOURCE_FILE_ID)
    ResponseEntity<DeleteGfcSourceFileResponseDTO> delete(@PathVariable UUID sourceFileId);
}
