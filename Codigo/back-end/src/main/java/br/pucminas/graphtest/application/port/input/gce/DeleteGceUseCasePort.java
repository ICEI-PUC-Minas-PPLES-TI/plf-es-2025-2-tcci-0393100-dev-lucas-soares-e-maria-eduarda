package br.pucminas.graphtest.application.port.input.gce;

import br.pucminas.graphtest.application.port.input.gce.records.DeleteGceInput;

public interface DeleteGceUseCasePort {
    void execute(DeleteGceInput input);
}
