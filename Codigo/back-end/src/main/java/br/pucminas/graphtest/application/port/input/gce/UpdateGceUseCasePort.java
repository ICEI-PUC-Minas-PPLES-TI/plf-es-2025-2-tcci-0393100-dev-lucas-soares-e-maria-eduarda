package br.pucminas.graphtest.application.port.input.gce;

import br.pucminas.graphtest.application.port.input.gce.records.GceOutput;
import br.pucminas.graphtest.application.port.input.gce.records.UpdateGceInput;

/**
 * Porta de entrada para atualizar metadados basicos de um GCE.
 */
public interface UpdateGceUseCasePort {

    GceOutput execute(UpdateGceInput input);
}
