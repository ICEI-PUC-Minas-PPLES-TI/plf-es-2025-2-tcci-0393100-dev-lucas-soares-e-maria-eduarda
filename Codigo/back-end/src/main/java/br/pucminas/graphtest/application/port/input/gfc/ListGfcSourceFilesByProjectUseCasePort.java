package br.pucminas.graphtest.application.port.input.gfc;

import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceFileOutput;

import java.util.List;
import java.util.UUID;

public interface ListGfcSourceFilesByProjectUseCasePort {

    List<GfcSourceFileOutput> execute(UUID projectId);
}
