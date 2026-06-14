package br.pucminas.graphtest.application.service.gfc.builder;

/**
 * Classifica arestas pendentes que precisam de tratamento especial durante a construcao do GFC.
 */
public enum PendingEdgeKind {

    NORMAL,
    BREAK,
    CONTINUE
}
