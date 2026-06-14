package br.pucminas.graphtest.application.port.input.gfc;

import br.pucminas.graphtest.application.port.input.gfc.records.CreateGfcSourceFileInput;
import br.pucminas.graphtest.application.port.input.gfc.records.CreateGfcSourceFileOutput;

/**
 * Porta de entrada para cadastrar arquivo-fonte Java da feature GFC.
 */
public interface CreateGfcSourceFileUseCasePort {

    CreateGfcSourceFileOutput execute(CreateGfcSourceFileInput input);
}
