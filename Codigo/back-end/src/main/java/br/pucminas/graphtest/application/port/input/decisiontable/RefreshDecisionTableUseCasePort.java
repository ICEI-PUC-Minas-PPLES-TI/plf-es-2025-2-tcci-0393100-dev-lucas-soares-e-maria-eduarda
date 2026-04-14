package br.pucminas.graphtest.application.port.input.decisiontable;

import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableOutput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableByGceIdInput;

/**
 * Porta de entrada do caso de uso responsavel por regenerar uma tabela de decisao a partir do GCE atual.
 */
public interface RefreshDecisionTableUseCasePort {

    /**
     * Regenera a tabela de decisao associada ao GCE informado.
     *
     * @param input identificador do GCE de origem
     * @return representacao atualizada da tabela
     */
    DecisionTableOutput execute(DecisionTableByGceIdInput input);
}
