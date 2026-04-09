package br.pucminas.graphtest.application.port.input.gce;

import br.pucminas.graphtest.application.port.input.gce.records.GceOutput;

import java.util.List;

public interface ListGcesUseCasePort {
    List<GceOutput> execute();
}
