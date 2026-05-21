package br.pucminas.graphtest.application.port.input.gfc;

import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceFileOutput;

import java.util.UUID;

public interface FindGfcSourceFileByIdUseCasePort {

    GfcSourceFileOutput execute(UUID sourceFileId);
}
