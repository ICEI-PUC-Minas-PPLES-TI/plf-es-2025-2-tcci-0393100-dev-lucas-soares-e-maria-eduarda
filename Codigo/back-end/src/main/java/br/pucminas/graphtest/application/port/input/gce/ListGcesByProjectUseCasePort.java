package br.pucminas.graphtest.application.port.input.gce;

import br.pucminas.graphtest.application.port.input.gce.records.GceOutput;
import br.pucminas.graphtest.application.port.input.gce.records.ListGcesByProjectInput;

import java.util.List;

public interface ListGcesByProjectUseCasePort {
    List<GceOutput> execute(ListGcesByProjectInput input);
}
