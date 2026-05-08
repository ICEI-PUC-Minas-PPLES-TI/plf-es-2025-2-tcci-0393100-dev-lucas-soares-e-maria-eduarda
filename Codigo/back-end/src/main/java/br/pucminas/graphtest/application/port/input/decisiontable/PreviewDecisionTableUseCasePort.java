package br.pucminas.graphtest.application.port.input.decisiontable;

import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableOutput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableByGceIdInput;

/**
 * Porta de entrada do caso de uso responsavel por montar uma pre-visualizacao da tabela sem persistencia.
 */
public interface PreviewDecisionTableUseCasePort {

    /**
     * Deriva em memoria a tabela de decisao para o GCE informado.
     *
     * @param input identificador do GCE de origem
     * @return representacao derivada da tabela
     */
    DecisionTableOutput execute(DecisionTableByGceIdInput input);
}
