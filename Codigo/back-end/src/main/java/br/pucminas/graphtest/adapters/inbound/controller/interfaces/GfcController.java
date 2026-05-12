package br.pucminas.graphtest.adapters.inbound.controller.interfaces;

import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcSourceCodeDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.GfcSourceMethodDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gfc.PreviewGfcDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.*;

public interface GfcController {

    @PostMapping(GFC_SOURCE)
    ResponseEntity<GfcSourceCodeDTO> source(@RequestParam("file") MultipartFile file);

    @PostMapping(GFC_SOURCE_METHODS)
    ResponseEntity<List<GfcSourceMethodDTO>> listMethods(@RequestParam("file") MultipartFile file);

    @PostMapping(GFC_PREVIEW)
    ResponseEntity<GfcDTO> preview(@Validated @RequestBody PreviewGfcDTO request);
}
