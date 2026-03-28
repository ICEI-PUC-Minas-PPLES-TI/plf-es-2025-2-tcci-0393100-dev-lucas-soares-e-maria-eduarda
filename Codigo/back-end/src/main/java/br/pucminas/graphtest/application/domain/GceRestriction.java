package br.pucminas.graphtest.application.domain;

import br.pucminas.graphtest.application.domain.enums.RestrictionTypeEnum;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa uma restricao formal aplicada a nos de um GCE.
 */
public class GceRestriction extends GceBaseEntity {

    private RestrictionTypeEnum type;
    private List<String> nodeCodes;

    /**
     * Cria uma restricao do GCE.
     *
     * @param id identificador persistido da restricao, quando existente
     * @param type tipo formal da restricao
     * @param nodeCodes codigos dos nos participantes da restricao
     */
    public GceRestriction(UUID id, RestrictionTypeEnum type, List<String> nodeCodes) {
        this.id = id;
        this.type = Objects.requireNonNull(type, "type e obrigatorio.");
        this.nodeCodes = normalizeNodeCodes(nodeCodes);

        if (this.nodeCodes.isEmpty()) {
            throw new IllegalArgumentException("Uma restricao deve referenciar ao menos um no.");
        }

        if ((type == RestrictionTypeEnum.REQUIRE || type == RestrictionTypeEnum.MASKS) && this.nodeCodes.size() != 2) {
            throw new IllegalArgumentException("Restricoes R e M devem possuir exatamente 2 nos.");
        }

        if ((type == RestrictionTypeEnum.EXCLUSIVE || type == RestrictionTypeEnum.INCLUSIVE || type == RestrictionTypeEnum.ONE_AND_ONLY_ONE) && this.nodeCodes.size() < 2) {
            throw new IllegalArgumentException("Restricoes E, I e O devem possuir pelo menos 2 nos.");
        }
    }

    private List<String> normalizeNodeCodes(List<String> nodeCodes) {
        Objects.requireNonNull(nodeCodes, "nodeCodes e obrigatorio.");

        LinkedHashSet<String> uniqueNodeCodes = new LinkedHashSet<>();
        for (String nodeCode : nodeCodes) {
            if (nodeCode == null || nodeCode.isBlank()) {
                throw new IllegalArgumentException("nodeCodes nao pode conter valores nulos ou vazios.");
            }
            String normalized = nodeCode.trim();
            if (!uniqueNodeCodes.add(normalized)) {
                throw new IllegalArgumentException("Uma restricao nao pode referenciar o mesmo no mais de uma vez.");
            }
        }

        return List.copyOf(new ArrayList<>(uniqueNodeCodes));
    }

    public String firstNode() {
        return nodeCodes.get(0);
    }

    public String secondNode() {
        return nodeCodes.get(1);
    }

    public RestrictionTypeEnum getType() {
        return type;
    }

    public List<String> getNodeCodes() {
        return nodeCodes;
    }

    public boolean isCauseRestriction() {
        return switch (type) {
            case EXCLUSIVE, INCLUSIVE, ONE_AND_ONLY_ONE, REQUIRE -> true;
            case MASKS -> false;
        };
    }

    public boolean isEffectRestriction() {
        return type == RestrictionTypeEnum.MASKS;
    }

    public boolean references(String nodeCode) {
        return nodeCodes.contains(nodeCode);
    }

    public boolean sameDefinition(GceRestriction other) {
        return other != null
                && type == other.type
                && nodeCodes.equals(other.nodeCodes);
    }
}
