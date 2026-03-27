package br.pucminas.graphtest.application.domain;

import br.pucminas.graphtest.application.domain.enums.GceNodeTypeEnum;
import br.pucminas.graphtest.application.domain.enums.GceOperatorTypeEnum;

import java.util.UUID;

public class GceNode extends BaseEntity{

    private String code;
    private String label;
    private GceNodeTypeEnum type;
    private GceOperatorTypeEnum operatorType;

    public GceNode() {
    }


    public GceNode(UUID id, String code, String label, GceNodeTypeEnum type, GceOperatorTypeEnum operatorType) {
        this.id = id;
        this.code = code;
        this.label = label;
        this.type = type;

        if (type == GceNodeTypeEnum.OPERATOR && operatorType == null) {
            throw new IllegalArgumentException("Nó operador deve possuir operatorType.");
        }
        if (type != GceNodeTypeEnum.OPERATOR && operatorType != null) {
            throw new IllegalArgumentException("Somente nós operadores podem possuir operatorType.");
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

    public UUID getId() {
        return id;
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

    public void setCode(String code) {
        this.code = code;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setType(GceNodeTypeEnum type) {
        this.type = type;
    }

    public void setOperatorType(GceOperatorTypeEnum operatorType) {
        this.operatorType = operatorType;
    }
}
