package br.pucminas.graphtest.application.domain.gfc.model;

import br.pucminas.graphtest.application.domain.gfc.enums.GfcEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.shared.model.BaseEntity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static br.pucminas.graphtest.application.domain.gfc.rules.GfcDomainRules.normalizeOptionalText;
import static br.pucminas.graphtest.application.domain.gfc.rules.GfcDomainRules.requireNonNull;
import static br.pucminas.graphtest.application.domain.gfc.rules.GfcDomainRules.requireText;

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
        this.sourceNodeCode = requireText(sourceNodeCode, "O codigo do no de origem");
        this.targetNodeCode = requireText(targetNodeCode, "O codigo do no de destino");
        this.type = requireNonNull(type, "O tipo da aresta e obrigatorio.");
        this.label = normalizeOptionalText(label);
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
                && type == other.type
                && Objects.equals(label, other.label);
    }
}
