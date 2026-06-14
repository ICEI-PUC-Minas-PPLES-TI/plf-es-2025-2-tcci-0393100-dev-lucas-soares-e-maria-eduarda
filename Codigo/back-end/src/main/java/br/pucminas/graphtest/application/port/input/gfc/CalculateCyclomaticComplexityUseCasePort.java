package br.pucminas.graphtest.application.port.input.gfc;

import br.pucminas.graphtest.application.port.input.gfc.records.CyclomaticComplexityOutput;

import java.util.UUID;

public interface CalculateCyclomaticComplexityUseCasePort {

    CyclomaticComplexityOutput execute(UUID projectId, UUID gfcId);
}
