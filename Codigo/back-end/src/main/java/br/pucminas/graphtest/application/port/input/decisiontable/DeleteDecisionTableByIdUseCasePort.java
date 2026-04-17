package br.pucminas.graphtest.application.port.input.decisiontable;

import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableByIdInput;

/**
 * Porta de entrada do caso de uso responsavel por excluir uma tabela de decisao pelo identificador proprio.
 */
public interface DeleteDecisionTableByIdUseCasePort {

    /**
     * Exclui a tabela identificada na entrada.
     *
     * @param input identificador da tabela
     */
    void execute(DecisionTableByIdInput input);
}
