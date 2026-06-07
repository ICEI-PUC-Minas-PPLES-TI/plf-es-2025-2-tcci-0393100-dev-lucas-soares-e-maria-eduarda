package br.pucminas.graphtest.application.port.input.decisiontable;

import br.pucminas.graphtest.application.port.input.decisiontable.records.GenerateFunctionalTestSignatureOutput;

import java.util.UUID;

public interface GenerateFunctionalTestSignatureUseCasePort {

    GenerateFunctionalTestSignatureOutput execute(UUID projectId, UUID decisionTableId);
}
