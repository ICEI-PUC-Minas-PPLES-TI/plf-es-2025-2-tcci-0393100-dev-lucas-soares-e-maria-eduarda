package br.pucminas.graphtest.application.domain;

import br.pucminas.graphtest.application.domain.enums.GceNodeTypeEnum;
import br.pucminas.graphtest.application.domain.enums.GceOperatorTypeEnum;

import java.util.Objects;

/**
 * Representa um no do Grafo de Causa e Efeito.
 */
public class GceNode extends GceBaseEntity {

    private String code;
    private String label;
    private GceNodeTypeEnum type;
    private GceOperatorTypeEnum operatorType;

    /**
     * Cria um no do GCE.
     *
     * @param id identificador persistido do no, quando existente
     * @param code codigo textual unico no contexto do grafo
     * @param label descricao legivel do no
     * @param type natureza do no no modelo
     * @param operatorType operador logico associado, quando aplicavel
     */
    public GceNode(Long id, String code, String label, GceNodeTypeEnum type, GceOperatorTypeEnum operatorType) {
        this.id = id;
        this.code = requireText(code, "code");
        this.label = requireText(label, "label");
        applyTypeAndOperator(type, operatorType);
    }

    /**
     * Fabrica um no do tipo causa.
     */
    public static GceNode cause(Long id, String code, String label) {
        return new GceNode(id, code, label, GceNodeTypeEnum.CAUSE, null);
    }

    /**
     * Fabrica um no do tipo efeito.
     */
    public static GceNode effect(Long id, String code, String label) {
        return new GceNode(id, code, label, GceNodeTypeEnum.EFFECT, null);
    }

    /**
     * Fabrica um no do tipo operador.
     */
    public static GceNode operator(Long id, String code, String label, GceOperatorTypeEnum operatorType) {
        return new GceNode(id, code, label, GceNodeTypeEnum.OPERATOR, operatorType);
    }

    private String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " e obrigatorio.");
        }
        return value.trim();
    }

    private void applyTypeAndOperator(GceNodeTypeEnum type, GceOperatorTypeEnum operatorType) {
        this.type = Objects.requireNonNull(type, "type e obrigatorio.");

        if (this.type == GceNodeTypeEnum.OPERATOR && operatorType == null) {
            throw new IllegalArgumentException("No operador deve possuir operatorType.");
        }
        if (this.type != GceNodeTypeEnum.OPERATOR && operatorType != null) {
            throw new IllegalArgumentException("Somente nos operadores podem possuir operatorType.");
        }

        this.operatorType = operatorType;
    }

    public boolean isCause() {
        return type == GceNodeTypeEnum.CAUSE;
    }

    public boolean isEffect() {
        return type == GceNodeTypeEnum.EFFECT;
    }

    public boolean isOperator() {
        return type == GceNodeTypeEnum.OPERATOR;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public GceNodeTypeEnum getType() {
        return type;
    }

    public GceOperatorTypeEnum getOperatorType() {
        return operatorType;
    }
}
