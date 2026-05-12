package br.pucminas.graphtest.application.service.gfc.interfaces;

import br.pucminas.graphtest.application.domain.gfc.model.Gfc;
import br.pucminas.graphtest.application.service.gfc.records.GfcGenerationInput;

/**
 * Servico de aplicacao responsavel por gerar um GFC a partir de codigo-fonte Java.
 */
public interface GfcGenerationService {

    Gfc generate(GfcGenerationInput input);
}
