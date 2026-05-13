package br.pucminas.graphtest.application.port.input.gfc;

import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceMethodDetailsOutput;

import java.util.UUID;

public interface GetGfcSourceMethodDetailsUseCasePort {

    GfcSourceMethodDetailsOutput execute(UUID sourceFileId, String methodSignature);
}
