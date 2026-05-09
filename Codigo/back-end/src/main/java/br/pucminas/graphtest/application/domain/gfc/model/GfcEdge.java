package br.pucminas.graphtest.application.domain.gfc.model;

import br.pucminas.graphtest.application.domain.gfc.enums.GfcEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.shared.model.BaseEntity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa uma aresta do fluxo de controle entre dois vertices do GFC.
 */
public class GfcEdge extends BaseEntity {

    private String sourceNodeCode;
    private String targetNodeCode;
    private GfcEdgeTypeEnum type;
    private String label;

    public GfcEdge(UUID id, String sourceNodeCode, String targetNodeCode, GfcEdgeTypeEnum type, String label) {
        this(id, sourceNodeCode, targetNodeCode, type, label, null, null);
    }

    public GfcEdge(UUID id,
                   String sourceNodeCode,
                   String targetNodeCode,
                   GfcEdgeTypeEnum type,
                   String label,
                   LocalDateTime createdAt,
                   LocalDateTime updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.sourceNodeCode = requireText(sourceNodeCode, "sourceNodeCode");
        this.targetNodeCode = requireText(targetNodeCode, "targetNodeCode");
        this.type = Objects.requireNonNull(type, "O tipo da aresta e obrigatorio.");
        this.label = normalizeLabel(label);
    }

    private String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(displayFieldName(field) + " e obrigatorio.");
        }
        return value.trim();
    }

    private String displayFieldName(String field) {
        return switch (field) {
            case "sourceNodeCode" -> "O codigo do no de origem";
            case "targetNodeCode" -> "O codigo do no de destino";
            default -> field;
        };
    }

    private String normalizeLabel(String value) {
        return value == null ? "" : value.trim();
    }

    public String getSourceNodeCode() {
        return sourceNodeCode;
    }

    public String getTargetNodeCode() {
        return targetNodeCode;
    }

    public GfcEdgeTypeEnum getType() {
        return type;
    }

    public String getLabel() {
        return label;
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

    public boolean sameSignature(GfcEdge other) {
        return other != null
                && sourceNodeCode.equals(other.sourceNodeCode)
                && targetNodeCode.equals(other.targetNodeCode)
                && type == other.type;
    }
}
