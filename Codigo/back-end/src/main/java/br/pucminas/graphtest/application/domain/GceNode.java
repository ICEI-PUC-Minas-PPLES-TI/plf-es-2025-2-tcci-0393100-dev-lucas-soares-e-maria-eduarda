package br.pucminas.graphtest.application.domain;

import br.pucminas.graphtest.application.domain.enums.GceNodeTypeEnum;
import br.pucminas.graphtest.application.domain.enums.GceOperatorTypeEnum;

import java.util.Objects;
import java.util.UUID;

/**
 * Representa um no do Grafo de Causa e Efeito.
 *
 * <p>Um no pode modelar uma causa, um efeito ou um operador logico, com o tipo
 * de operador informado apenas quando o no for do tipo operador.</p>
 */
public class GceNode extends BaseEntity {

    private String code;
    private String label;
    private GceNodeTypeEnum type;
    private GceOperatorTypeEnum operatorType;

    /**
     * Cria um no do GCE.
     *
     * @param id identificador do no
     * @param code codigo textual unico no contexto do grafo
     * @param label descricao legivel do no
     * @param type natureza do no no modelo
     * @param operatorType operador logico associado, quando aplicavel
     */
    public GceNode(UUID id, String code, String label, GceNodeTypeEnum type, GceOperatorTypeEnum operatorType) {
        this.id = requireUuid(id);
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

    /**
     * Fabrica um no do tipo efeito.
     */
    public static GceNode effect(UUID id, String code, String label) {
        return new GceNode(id, code, label, GceNodeTypeEnum.EFFECT, null);
    }

    /**
     * Fabrica um no do tipo operador.
     */
    public static GceNode operator(UUID id, String code, String label, GceOperatorTypeEnum operatorType) {
        return new GceNode(id, code, label, GceNodeTypeEnum.OPERATOR, operatorType);
    }

    /**
     * Garante que o identificador do no foi informado.
     *
     * @param value identificador recebido
     * @return identificador validado
     */
    private UUID requireUuid(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("id e obrigatorio.");
        }
        return value;
    }

    /**
     * Garante que um campo textual obrigatorio do no foi informado.
     *
     * @param value valor textual recebido
     * @param field nome do campo validado
     * @return texto normalizado
     */
    private String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " e obrigatorio.");
        }
        return value.trim();
    }

    /**
     * Aplica e valida o tipo do no e seu operador associado.
     *
     * @param type tipo do no
     * @param operatorType operador logico associado, quando aplicavel
     */
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

    /**
     * Indica se o no representa uma causa.
     *
     * @return {@code true} quando o no for do tipo causa
     */
    public boolean isCause() {
        return type == GceNodeTypeEnum.CAUSE;
    }

    /**
     * Indica se o no representa um efeito.
     *
     * @return {@code true} quando o no for do tipo efeito
     */
    public boolean isEffect() {
        return type == GceNodeTypeEnum.EFFECT;
    }

    /**
     * Indica se o no representa um operador logico.
     *
     * @return {@code true} quando o no for do tipo operador
     */
    public boolean isOperator() {
        return type == GceNodeTypeEnum.OPERATOR;
    }

    /**
     * Retorna o codigo do no.
     *
     * @return codigo textual do no
     */
    public String getCode() {
        return code;
    }

    /**
     * Retorna o identificador do no.
     *
     * @return identificador do no
     */
    public UUID getId() {
        return id;
    }

    /**
     * Retorna o rotulo legivel do no.
     *
     * @return descricao do no
     */
    public String getLabel() {
        return label;
    }

    /**
     * Retorna o tipo do no.
     *
     * @return tipo do no
     */
    public GceNodeTypeEnum getType() {
        return type;
    }

    /**
     * Retorna o operador logico associado ao no, quando existir.
     *
     * @return operador associado ou {@code null} para nos nao operadores
     */
    public GceOperatorTypeEnum getOperatorType() {
        return operatorType;
    }
}
