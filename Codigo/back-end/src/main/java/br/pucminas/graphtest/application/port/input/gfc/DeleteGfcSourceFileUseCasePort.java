package br.pucminas.graphtest.application.port.input.gfc;

import java.util.UUID;

public interface DeleteGfcSourceFileUseCasePort {

    void execute(UUID projectId, UUID sourceFileId);
}
