package br.pucminas.graphtest.application.port.input.decisiontable;

import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableByIdInput;

/**
 * Caso de uso responsavel por verificar se uma tabela de decisao segue sincronizada com o GCE associado.
 */
public interface FindDecisionTableStatusByIdUseCasePort {

    /**
     * Verifica se a tabela de decisao permanece sincronizada com o GCE de origem.
     *
     * @param input identificador da tabela de decisao
     * @return {@code true} quando a tabela estiver sincronizada; caso contrario, {@code false}
     */
    boolean execute(DecisionTableByIdInput input);
}
