package br.pucminas.graphtest.application.domain.gfc.enums;

/**
 * Enumera os tipos iniciais de aresta do Grafo de Fluxo de Controle.
 */
public enum GfcEdgeTypeEnum {

    SEQUENTIAL,
    TRUE_BRANCH,
    FALSE_BRANCH,
    LOOP_BACK,
    LOOP_BODY,
    LOOP_EXIT,
    CASE_BRANCH,
    DEFAULT_BRANCH,
    TRY_BRANCH,
    CATCH_BRANCH,
    FINALLY_BRANCH,
    BREAK_FLOW,
    CONTINUE_FLOW,
    THROW_FLOW;

}
