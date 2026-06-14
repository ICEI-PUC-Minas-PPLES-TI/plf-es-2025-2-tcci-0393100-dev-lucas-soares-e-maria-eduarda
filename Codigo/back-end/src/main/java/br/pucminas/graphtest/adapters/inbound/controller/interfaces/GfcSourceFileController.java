package br.pucminas.graphtest.adapters.inbound.controller.interfaces;

import br.pucminas.graphtest.adapters.inbound.dto.gfc.CreateGfcSourceFileResponseDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.DeleteGfcSourceFileResponseDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcSourceCodeDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcSourceFileDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcSourceMethodDetailsDTO;
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

import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.JAVA_FILE_ID;

public interface GfcSourceFileController {

    @PostMapping
    ResponseEntity<CreateGfcSourceFileResponseDTO> createSourceFile(@PathVariable UUID projectId,
                                                                    @RequestParam("file") MultipartFile file);

    @GetMapping(JAVA_FILE_ID)
    ResponseEntity<GfcSourceFileDTO> findById(@PathVariable UUID projectId, @PathVariable("fileId") UUID sourceFileId);

    @GetMapping
    ResponseEntity<List<GfcSourceFileDTO>> listByProject(@PathVariable UUID projectId);

    @GetMapping(JAVA_FILE_ID + "/source-code")
    ResponseEntity<GfcSourceCodeDTO> getSourceCode(@PathVariable UUID projectId,
                                                   @PathVariable("fileId") UUID sourceFileId);

    @GetMapping(JAVA_FILE_ID + "/methods")
    ResponseEntity<List<GfcSourceMethodDTO>> listMethods(@PathVariable UUID projectId,
                                                         @PathVariable("fileId") UUID sourceFileId);

    @GetMapping(JAVA_FILE_ID + "/method")
    ResponseEntity<GfcSourceMethodDetailsDTO> getMethodDetails(@PathVariable UUID projectId,
                                                               @PathVariable("fileId") UUID sourceFileId,
                                                               @RequestParam("signature") String methodSignature);

    @DeleteMapping(JAVA_FILE_ID)
    ResponseEntity<DeleteGfcSourceFileResponseDTO> delete(@PathVariable UUID projectId,
                                                          @PathVariable("fileId") UUID sourceFileId);
}
