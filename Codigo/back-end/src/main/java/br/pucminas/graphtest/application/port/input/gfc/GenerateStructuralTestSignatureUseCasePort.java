package br.pucminas.graphtest.application.port.input.gfc;

import br.pucminas.graphtest.application.port.input.gfc.records.GenerateStructuralTestSignatureOutput;

import java.util.UUID;

public interface GenerateStructuralTestSignatureUseCasePort {

    GenerateStructuralTestSignatureOutput execute(UUID gfcId);
}
