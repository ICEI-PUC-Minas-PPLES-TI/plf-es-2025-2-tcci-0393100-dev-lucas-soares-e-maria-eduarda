package br.pucminas.graphtest.application.domain.gce.model;

import br.pucminas.graphtest.application.domain.shared.model.BaseEntity;
import br.pucminas.graphtest.application.domain.gce.enums.GceNodeTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.GceOperatorTypeEnum;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa um no do Grafo de Causa e Efeito.
 */
public class GceNode extends BaseEntity {

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
    public GceNode(UUID id, String code, String label, GceNodeTypeEnum type, GceOperatorTypeEnum operatorType) {
        this(id, code, label, type, operatorType, null, null);
    }

    public GceNode(UUID id,
                   String code,
                   String label,
                   GceNodeTypeEnum type,
                   GceOperatorTypeEnum operatorType,
                   LocalDateTime createdAt,
                   LocalDateTime updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.code = requireText(code, "code");
        this.label = requireText(label, "label");
        applyTypeAndOperator(type, operatorType);
    }

    /**
     * Fabrica um no do tipo causa.
     */
    public static GceNode cause(UUID id, String code, String label) {
        return new GceNode(id, code, label, GceNodeTypeEnum.CAUSE, null);
    }

    public static GceNode cause(UUID id, String code, String label, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new GceNode(id, code, label, GceNodeTypeEnum.CAUSE, null, createdAt, updatedAt);
    }

    /**
     * Fabrica um no do tipo efeito.
     */
    public static GceNode effect(UUID id, String code, String label) {
        return new GceNode(id, code, label, GceNodeTypeEnum.EFFECT, null);
    }

    public static GceNode effect(UUID id, String code, String label, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new GceNode(id, code, label, GceNodeTypeEnum.EFFECT, null, createdAt, updatedAt);
    }

    /**
     * Fabrica um no do tipo operador.
     */
    public static GceNode operator(UUID id, String code, String label, GceOperatorTypeEnum operatorType) {
        return new GceNode(id, code, label, GceNodeTypeEnum.OPERATOR, operatorType);
    }

    public static GceNode operator(UUID id,
                                   String code,
                                   String label,
                                   GceOperatorTypeEnum operatorType,
                                   LocalDateTime createdAt,
                                   LocalDateTime updatedAt) {
        return new GceNode(id, code, label, GceNodeTypeEnum.OPERATOR, operatorType, createdAt, updatedAt);
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
