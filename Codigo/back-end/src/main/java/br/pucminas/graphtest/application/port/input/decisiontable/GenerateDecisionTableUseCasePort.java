package br.pucminas.graphtest.application.port.input.decisiontable;

import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableOutput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.GenerateDecisionTableInput;

/**
 * Porta de entrada do caso de uso responsavel por gerar uma tabela de decisao a partir de um GCE.
 */
public interface GenerateDecisionTableUseCasePort {

    /**
     * Gera e persiste a tabela de decisao derivada do GCE informado.
     *
     * @param input dados de entrada para geracao da tabela
     * @return representacao da tabela gerada
     */
    DecisionTableOutput execute(GenerateDecisionTableInput input);
}
