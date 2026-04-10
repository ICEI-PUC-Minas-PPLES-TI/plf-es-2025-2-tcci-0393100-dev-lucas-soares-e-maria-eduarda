package br.pucminas.graphtest.application.port.input.gce;

import br.pucminas.graphtest.application.port.input.gce.records.GceOutput;
import br.pucminas.graphtest.application.port.input.gce.records.UpdateGceDetailsInput;

public interface PatchGceDetailsUseCasePort {
    GceOutput execute(UpdateGceDetailsInput input);
}
