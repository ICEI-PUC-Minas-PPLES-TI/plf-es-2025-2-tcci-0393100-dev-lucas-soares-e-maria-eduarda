package br.pucminas.graphtest.application.domain.decisiontable.enums;

/**
 * Define os valores suportados pelas celulas de acao da tabela de decisao.
 */
public enum DecisionTableActionValueEnum {

    YES("S"),
    NO("N");

    private final String code;

    DecisionTableActionValueEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
