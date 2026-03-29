package br.pucminas.graphtest.application.port.input.gce;

import br.pucminas.graphtest.application.port.input.gce.records.GceOutput;
import br.pucminas.graphtest.application.port.input.gce.records.UpdateGceNodeInput;

/**
 * Porta de entrada para atualizar um no de um GCE existente.
 */
public interface UpdateGceNodeUseCasePort {

    GceOutput execute(UpdateGceNodeInput input);
}
