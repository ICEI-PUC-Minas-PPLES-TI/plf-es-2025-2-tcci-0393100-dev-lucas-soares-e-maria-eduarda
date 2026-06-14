package br.pucminas.graphtest.application.port.input.decisiontable;

import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableByIdInput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableOutput;

/**
 * Porta de entrada do caso de uso responsavel por buscar uma tabela de decisao pelo identificador proprio.
 */
public interface FindDecisionTableByIdUseCasePort {

    /**
     * Busca a tabela de decisao pelo identificador informado.
     *
     * @param input identificador da tabela
     * @return representacao da tabela encontrada
     */
    DecisionTableOutput execute(DecisionTableByIdInput input);
}
