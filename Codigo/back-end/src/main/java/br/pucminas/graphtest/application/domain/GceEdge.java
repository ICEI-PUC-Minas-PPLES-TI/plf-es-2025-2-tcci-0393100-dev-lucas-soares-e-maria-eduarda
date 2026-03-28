package br.pucminas.graphtest.application.domain;

import br.pucminas.graphtest.application.domain.enums.GceEdgeTypeEnum;

import java.util.Objects;
import java.util.UUID;

/**
 * Representa uma aresta do GCE ligando um no de origem a um no de destino.
 *
 * <p>A aresta tambem carrega o tipo logico da conexao, permitindo identidade
 * direta ou negacao do valor propagado.</p>
 */
public class GceEdge extends BaseEntity {
    private UUID sourceNodeId;
    private UUID targetNodeId;
    private GceEdgeTypeEnum type;

    /**
     * Cria uma aresta entre dois nos distintos.
     *
     * @param id identificador da aresta
     * @param sourceNodeId identificador do no de origem
     * @param targetNodeId identificador do no de destino
     * @param type comportamento logico da propagacao
     */
    public GceEdge(UUID id, UUID sourceNodeId, UUID targetNodeId, GceEdgeTypeEnum type) {
        this.id = requireUuid(id, "id");
        this.sourceNodeId = requireUuid(sourceNodeId, "sourceNodeId");
        this.targetNodeId = requireUuid(targetNodeId, "targetNodeId");
        this.type = Objects.requireNonNull(type, "type e obrigatorio.");

        if (this.sourceNodeId.equals(this.targetNodeId)) {
            throw new IllegalArgumentException("Uma aresta nao pode ligar um no a ele mesmo.");
        }
    }

    /**
     * Garante que um identificador obrigatorio da aresta foi informado.
     *
     * @param value valor recebido
     * @param field nome do campo validado
     * @return identificador validado
     */
    private UUID requireUuid(UUID value, String field) {
        if (value == null) {
            throw new IllegalArgumentException(field + " e obrigatorio.");
        }
        return value;
    }

    /**
     * Retorna o identificador da aresta.
     *
     * @return identificador da aresta
     */
    public UUID getId() {
        return id;
    }

    /**
     * Retorna o identificador do no de origem.
     *
     * @return identificador do no de origem
     */
    public UUID getSourceNodeId() {
        return sourceNodeId;
    }

    /**
     * Retorna o identificador do no de destino.
     *
     * @return identificador do no de destino
     */
    public UUID getTargetNodeId() {
        return targetNodeId;
    }

    /**
     * Retorna o tipo logico da aresta.
     *
     * @return tipo da aresta
     */
    public GceEdgeTypeEnum getType() {
        return type;
    }

    /**
     * Indica se a aresta propaga o valor sem negacao.
     *
     * @return {@code true} para arestas de identidade
     */
    public boolean isIdentity() {
        return type == GceEdgeTypeEnum.IDENTITY;
    }

    /**
     * Indica se a aresta inverte o valor booleano propagado.
     *
     * @return {@code true} para arestas negadas
     */
    public boolean isNegated() {
        return type == GceEdgeTypeEnum.NEGATED;
    }

    /**
     * Verifica se a aresta parte do no informado.
     *
     * @param nodeId identificador a ser comparado com a origem
     * @return {@code true} quando a origem coincide
     */
    public boolean startsFrom(UUID nodeId) {
        return sourceNodeId.equals(nodeId);
    }

    /**
     * Verifica se a aresta chega ao no informado.
     *
     * @param nodeId identificador a ser comparado com o destino
     * @return {@code true} quando o destino coincide
     */
    public boolean targets(UUID nodeId) {
        return targetNodeId.equals(nodeId);
    }

    /**
     * Verifica se a aresta referencia o no informado como origem ou destino.
     *
     * @param nodeId identificador do no consultado
     * @return {@code true} quando a aresta referencia o no
     */
    public boolean references(UUID nodeId) {
        return startsFrom(nodeId) || targets(nodeId);
    }

    /**
     * Compara a assinatura estrutural desta aresta com outra aresta.
     *
     * @param other outra aresta a ser comparada
     * @return {@code true} quando origem, destino e tipo coincidirem
     */
    public boolean sameSignature(GceEdge other) {
        return other != null
                && sourceNodeId.equals(other.sourceNodeId)
                && targetNodeId.equals(other.targetNodeId)
                && type == other.type;
    }
}
