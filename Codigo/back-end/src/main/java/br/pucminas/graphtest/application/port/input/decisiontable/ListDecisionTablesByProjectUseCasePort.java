package br.pucminas.graphtest.application.port.input.decisiontable;

import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableOutput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.ListDecisionTablesByProjectInput;

import java.util.List;

/**
 * Porta de entrada do caso de uso responsavel por listar tabelas de decisao de um projeto.
 */
public interface ListDecisionTablesByProjectUseCasePort {

    /**
     * Lista as tabelas de decisao associadas ao projeto informado.
     *
     * @param input identificador do projeto
     * @return tabelas encontradas no projeto
     */
    List<DecisionTableOutput> execute(ListDecisionTablesByProjectInput input);
}
