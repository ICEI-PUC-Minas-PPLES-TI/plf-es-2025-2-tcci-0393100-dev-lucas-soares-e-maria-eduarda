package br.pucminas.graphtest.application.port.input.decisiontable;

import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableByGceIdInput;

/**
 * Porta de entrada do caso de uso responsavel por excluir a tabela de decisao vinculada a um GCE.
 */
public interface DeleteDecisionTableByGceIdUseCasePort {

    /**
     * Remove a tabela de decisao associada ao GCE informado.
     *
     * @param input identificador do GCE de origem
     */
    void execute(DecisionTableByGceIdInput input);
}
