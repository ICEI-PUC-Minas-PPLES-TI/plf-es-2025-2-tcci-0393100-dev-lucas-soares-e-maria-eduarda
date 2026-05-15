package br.pucminas.graphtest.application.service.gfc.builder;

/**
 * Contexto de excecao ativo durante a construcao do GFC.
 */
class ExceptionFlowContext {

    private final String catchNodeCode;
    private final String finallyNodeCode;
    private boolean exceptionalFlowToFinally;

    ExceptionFlowContext(String catchNodeCode, String finallyNodeCode) {
        this.catchNodeCode = catchNodeCode;
        this.finallyNodeCode = finallyNodeCode;
    }

    String catchNodeCode() {
        return catchNodeCode;
    }

    String finallyNodeCode() {
        return finallyNodeCode;
    }

    boolean hasCatch() {
        return catchNodeCode != null;
    }

    boolean hasFinally() {
        return finallyNodeCode != null;
    }

    void markExceptionalFlowToFinally() {
        this.exceptionalFlowToFinally = true;
    }

    boolean hasExceptionalFlowToFinally() {
        return exceptionalFlowToFinally;
    }
}
