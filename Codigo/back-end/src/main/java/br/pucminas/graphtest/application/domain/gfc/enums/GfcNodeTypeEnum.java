package br.pucminas.graphtest.application.domain.gfc.enums;

/**
 * Enumera os tipos iniciais de no do Grafo de Fluxo de Controle.
 */
public enum GfcNodeTypeEnum {

    START,
    END,
    STATEMENT,
    DECISION,
    LOOP,
    RETURN,
    BREAK,
    CONTINUE,
    THROW,
    SWITCH,
    CASE,
    CASE_BLOCK,
    TRY,
    CATCH,
    FINALLY,
    TERNARY;

}
