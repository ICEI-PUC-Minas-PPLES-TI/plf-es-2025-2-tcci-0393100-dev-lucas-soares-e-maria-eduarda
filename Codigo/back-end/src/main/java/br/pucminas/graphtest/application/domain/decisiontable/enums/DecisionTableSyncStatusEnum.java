package br.pucminas.graphtest.application.domain.decisiontable.enums;

/**
 * Representa o estado de sincronizacao entre a tabela de decisao e o GCE de origem.
 */
public enum DecisionTableSyncStatusEnum {

    UP_TO_DATE,
    STALE
}
