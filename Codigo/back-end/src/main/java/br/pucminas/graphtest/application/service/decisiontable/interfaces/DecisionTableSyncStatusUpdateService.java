package br.pucminas.graphtest.application.service.decisiontable.interfaces;

import br.pucminas.graphtest.application.domain.gce.model.Gce;

import java.util.UUID;

/**
 * Servico responsavel por atualizar o estado de sincronizacao persistido da tabela de decisao
 * quando o GCE associado sofre mutacoes estruturais.
 */
public interface DecisionTableSyncStatusUpdateService {

    /**
     * Marca como desatualizada a tabela de decisao atualmente associada ao GCE informado, quando existir.
     *
     * @param gceId identificador do GCE alterado
     */
    void markDecisionTableAsStaleByGceId(UUID gceId);

    /**
     * Indica se houve alteracao no GCE que afeta semanticamente a tabela de decisao derivada.
     * Nome e descricao do GCE ficam fora dessa comparacao.
     *
     * @param previousGraph estado anterior do GCE
     * @param currentGraph  estado atual do GCE
     * @return {@code true} quando houve mudanca relevante para a tabela de decisao
     */
    boolean hasDecisionTableRelevantChanges(Gce previousGraph, Gce currentGraph);
}
