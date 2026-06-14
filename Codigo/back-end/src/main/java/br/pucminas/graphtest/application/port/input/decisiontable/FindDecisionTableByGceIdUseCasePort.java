package br.pucminas.graphtest.application.port.input.decisiontable;

import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableOutput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableByGceIdInput;

/**
 * Porta de entrada do caso de uso responsavel por buscar a tabela de decisao vinculada a um GCE.
 */
public interface FindDecisionTableByGceIdUseCasePort {

    /**
     * Busca a tabela de decisao associada ao GCE informado.
     *
     * @param input identificador do GCE de origem
     * @return representacao da tabela encontrada
     */
    DecisionTableOutput execute(DecisionTableByGceIdInput input);
}
