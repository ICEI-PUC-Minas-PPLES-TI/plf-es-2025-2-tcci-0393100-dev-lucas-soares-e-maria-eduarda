package br.pucminas.graphtest.application.port.input.gce;

import br.pucminas.graphtest.application.port.input.gce.records.AddNodeToGceInput;
import br.pucminas.graphtest.application.port.input.gce.records.GceOutput;

/**
 * Porta de entrada para adicionar um no a um GCE existente.
 */
public interface AddNodeToGceUseCasePort {

    GceOutput execute(AddNodeToGceInput input);
}
