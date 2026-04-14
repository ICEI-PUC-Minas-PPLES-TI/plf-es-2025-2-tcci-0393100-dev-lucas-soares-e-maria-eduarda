package br.pucminas.graphtest.application.service.decisiontable.interfaces;

import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTable;
import br.pucminas.graphtest.application.domain.gce.model.Gce;

/**
 * Servico responsavel por derivar uma tabela de decisao a partir de um GCE.
 */
public interface DecisionTableDerivationService {

    /**
     * Deriva uma tabela de decisao a partir do grafo informado.
     *
     * @param graph GCE de origem
     * @param currentTable tabela atual associada ao grafo, quando existir
     * @return agregado derivado pronto para persistencia ou preview
     */
    DecisionTable derive(Gce graph, DecisionTable currentTable);
}
