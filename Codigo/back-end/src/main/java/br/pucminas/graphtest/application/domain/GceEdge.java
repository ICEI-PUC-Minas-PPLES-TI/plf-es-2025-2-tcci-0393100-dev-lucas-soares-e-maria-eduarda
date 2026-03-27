package br.pucminas.graphtest.application.domain;

import br.pucminas.graphtest.application.domain.enums.GceEdgeTypeEnum;

import java.util.UUID;

public class GceEdge extends BaseEntity{
    private UUID sourceNodeId;
    private UUID targetNodeId;
    private GceEdgeTypeEnum type;

    public GceEdge() {
    }

    public GceEdge(UUID id, UUID sourceNodeId, UUID targetNodeId, GceEdgeTypeEnum type) {
        this.id = id;
        this.sourceNodeId = sourceNodeId;
        this.targetNodeId = sourceNodeId;
        this.type = type;

        if (sourceNodeId.equals(targetNodeId)) {
            throw new IllegalArgumentException("Uma aresta não pode ligar um nó a ele mesmo.");
        }
    }


    public UUID getId() {
        return id;
    }

    public UUID getSourceNodeId() {
        return sourceNodeId;
    }

    public UUID getTargetNodeId() {
        return targetNodeId;
    }

    public GceEdgeTypeEnum getType() {
        return type;
    }

    public void setType(GceEdgeTypeEnum type) {
        this.type = type;
    }
}
