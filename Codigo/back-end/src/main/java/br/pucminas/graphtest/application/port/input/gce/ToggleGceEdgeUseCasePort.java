package br.pucminas.graphtest.application.port.input.gce;

import br.pucminas.graphtest.application.port.input.gce.records.GceOutput;
import br.pucminas.graphtest.application.port.input.gce.records.ToggleGceEdgeInput;

/**
 * Porta de entrada para inverter o tipo de uma aresta de um GCE existente.
 */
public interface ToggleGceEdgeUseCasePort {

    GceOutput execute(ToggleGceEdgeInput input);
}
