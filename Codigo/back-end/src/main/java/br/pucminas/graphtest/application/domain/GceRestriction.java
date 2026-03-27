package br.pucminas.graphtest.application.domain;

import br.pucminas.graphtest.application.domain.enums.RestrictionTypeEnum;
import java.util.List;
import java.util.UUID;

public class GceRestriction extends BaseEntity{
    private RestrictionTypeEnum type;
    private List<UUID> nodeIds;


    public GceRestriction(){
    }

    public GceRestriction(UUID id, RestrictionTypeEnum type, List<UUID> nodeIds) {
        this.id = id;
        this.type = type;
        this.nodeIds = nodeIds;

        if (this.nodeIds.isEmpty()) {
            throw new IllegalArgumentException("Uma restrição deve referenciar ao menos um nó.");
        }

        if ((type == RestrictionTypeEnum.REQUIRE || type == RestrictionTypeEnum.MASKS) && this.nodeIds.size() != 2) {
            throw new IllegalArgumentException("Restrição de dependência (R) e de mascaramento (M) devem possuir exatamente 2 nós.");
        }

        if ((type == RestrictionTypeEnum.EXCLUSIVE || type == RestrictionTypeEnum.INCLUSIVE || type == RestrictionTypeEnum.ONE_AND_ONLY_ONE) && this.nodeIds.size() < 2) {
            throw new IllegalArgumentException("Restrições de dependência (R), inclusiva (I) e de uma e somente uma (O) devem possuir pelo menos 2 nós.");
        }
    }

    public UUID getId() {
        return id;
    }


    public UUID firstNode() {
        return nodeIds.get(0);
    }

    public UUID secondNode() {
        return nodeIds.get(1);
    }


    public RestrictionTypeEnum getType() {
        return type;
    }

    public List<UUID> getNodeIds() {
        return nodeIds;
    }
}
