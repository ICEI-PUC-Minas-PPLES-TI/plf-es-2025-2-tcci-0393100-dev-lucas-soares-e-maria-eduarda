package br.pucminas.graphtest.application.port.input.decisiontable;

import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableOutput;

import java.util.List;

public interface ListDecisionTablesUseCasePort {

    List<DecisionTableOutput> execute();
}
