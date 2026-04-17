package br.pucminas.graphtest.application.service.decisiontable.interfaces;

import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTable;
import br.pucminas.graphtest.application.domain.gce.model.Gce;

/**
 * Servico responsavel por verificar se a tabela de decisao continua sincronizada com o GCE de origem.
 */
public interface DecisionTableSyncService {

    /**
     * Indica se a tabela de decisao informada esta desatualizada em relacao ao GCE.
     *
     * @param decisionTable tabela de decisao persistida
     * @param graph GCE atual, quando existir
     * @return {@code true} quando a tabela estiver stale
     */
    boolean isStale(DecisionTable decisionTable, Gce graph);
}
