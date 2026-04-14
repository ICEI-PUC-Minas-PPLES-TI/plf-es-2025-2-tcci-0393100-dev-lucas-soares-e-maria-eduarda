package br.pucminas.graphtest.application.domain.decisiontable.enums;

/**
 * Define os valores suportados pelas celulas de condicao da tabela de decisao.
 */
public enum DecisionTableConditionValueEnum {

    YES("S"),
    NO("N"),
    IRRELEVANT("-");

    private final String code;

    DecisionTableConditionValueEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
