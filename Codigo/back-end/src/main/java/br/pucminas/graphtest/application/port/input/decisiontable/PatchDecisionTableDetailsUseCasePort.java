package br.pucminas.graphtest.application.port.input.decisiontable;

import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableOutput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.UpdateDecisionTableDetailsInput;

public interface PatchDecisionTableDetailsUseCasePort {
    DecisionTableOutput execute(UpdateDecisionTableDetailsInput input);
}
