package br.pucminas.graphtest.application.port.input.gfc;

import br.pucminas.graphtest.application.port.input.gfc.records.CreateGfcInput;
import br.pucminas.graphtest.application.port.input.gfc.records.CreateGfcOutput;

/**
 * Porta de entrada do caso de uso responsavel por criar e persistir um GFC.
 */
public interface CreateGfcUseCasePort {

    CreateGfcOutput execute(CreateGfcInput input);
}
