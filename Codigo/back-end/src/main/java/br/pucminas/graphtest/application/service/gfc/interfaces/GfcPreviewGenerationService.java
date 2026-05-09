package br.pucminas.graphtest.application.service.gfc.interfaces;

import br.pucminas.graphtest.application.domain.gfc.model.Gfc;
import br.pucminas.graphtest.application.port.input.gfc.records.PreviewGfcInput;

/**
 * Servico de aplicacao responsavel por gerar uma pre-visualizacao de GFC.
 */
public interface GfcPreviewGenerationService {

    Gfc generate(PreviewGfcInput input);
}
