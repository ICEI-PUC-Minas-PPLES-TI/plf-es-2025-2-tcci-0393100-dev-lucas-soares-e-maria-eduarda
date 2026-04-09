package br.pucminas.graphtest.application.domain.gce.model;

import br.pucminas.graphtest.application.domain.shared.model.BaseEntity;
import br.pucminas.graphtest.application.domain.gce.enums.GceEdgeTypeEnum;

import java.util.Objects;
import java.util.UUID;

/**
 * Representa uma aresta do GCE ligando um no de origem a um no de destino.
 */
public class GceEdge extends BaseEntity {

    private String sourceNodeCode;
    private String targetNodeCode;
    private GceEdgeTypeEnum type;

    /**
     * Cria uma aresta entre dois nos distintos.
     *
     * @param id identificador persistido da aresta, quando existente
     * @param sourceNodeCode codigo do no de origem
     * @param targetNodeCode codigo do no de destino
     * @param type comportamento logico da propagacao
     */
    public GceEdge(UUID id, String sourceNodeCode, String targetNodeCode, GceEdgeTypeEnum type) {
        this.id = id;
        this.sourceNodeCode = requireText(sourceNodeCode, "sourceNodeCode");
        this.targetNodeCode = requireText(targetNodeCode, "targetNodeCode");
        this.type = Objects.requireNonNull(type, "type e obrigatorio.");

        if (this.sourceNodeCode.equals(this.targetNodeCode)) {
            throw new IllegalArgumentException("Uma aresta nao pode ligar um no a ele mesmo.");
        }
    }

    private String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " e obrigatorio.");
        }
        return value.trim();
    }

    public String getSourceNodeCode() {
        return sourceNodeCode;
    }

    public String getTargetNodeCode() {
        return targetNodeCode;
    }

    public GceEdgeTypeEnum getType() {
        return type;
    }

    public boolean isIdentity() {
        return type == GceEdgeTypeEnum.IDENTITY;
    }

    public boolean isNegated() {
        return type == GceEdgeTypeEnum.NEGATED;
    }

    public boolean startsFrom(String nodeCode) {
        return sourceNodeCode.equals(nodeCode);
    }

    public boolean targets(String nodeCode) {
        return targetNodeCode.equals(nodeCode);
    }

    public boolean references(String nodeCode) {
        return startsFrom(nodeCode) || targets(nodeCode);
    }

    public boolean sameSignature(GceEdge other) {
        return other != null
                && sourceNodeCode.equals(other.sourceNodeCode)
                && targetNodeCode.equals(other.targetNodeCode)
                && type == other.type;
    }
}
