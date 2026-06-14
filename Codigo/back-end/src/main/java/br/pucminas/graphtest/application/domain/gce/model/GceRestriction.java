package br.pucminas.graphtest.application.domain.gce.model;

import br.pucminas.graphtest.application.domain.shared.model.BaseEntity;
import br.pucminas.graphtest.application.domain.gce.enums.RestrictionTypeEnum;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa uma restricao formal aplicada a nos de um GCE.
 */
public class GceRestriction extends BaseEntity {

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
        this(id, type, nodeCodes, null, null);
    }

    public GceRestriction(UUID id,
                          RestrictionTypeEnum type,
                          List<String> nodeCodes,
                          LocalDateTime createdAt,
                          LocalDateTime updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.type = Objects.requireNonNull(type, "O tipo da restrição é obrigatório.");
        this.nodeCodes = normalizeNodeCodes(nodeCodes);

        if (this.nodeCodes.isEmpty()) {
            throw new IllegalArgumentException("Uma restrição deve referenciar pelo menos um nó.");
        }

        if ((type == RestrictionTypeEnum.REQUIRE || type == RestrictionTypeEnum.MASKS) && this.nodeCodes.size() != 2) {
            throw new IllegalArgumentException("As restrições REQUIRE e MASKS devem possuir exatamente 2 nós.");
        }

        if ((type == RestrictionTypeEnum.EXCLUSIVE || type == RestrictionTypeEnum.INCLUSIVE || type == RestrictionTypeEnum.ONE_AND_ONLY_ONE) && this.nodeCodes.size() < 2) {
            throw new IllegalArgumentException("As restrições EXCLUSIVE, INCLUSIVE e ONE_AND_ONLY_ONE devem possuir pelo menos 2 nós.");
        }
    }

    private List<String> normalizeNodeCodes(List<String> nodeCodes) {
        Objects.requireNonNull(nodeCodes, "Os códigos dos nós da restrição são obrigatórios.");

        LinkedHashSet<String> uniqueNodeCodes = new LinkedHashSet<>();
        for (String nodeCode : nodeCodes) {
            if (nodeCode == null || nodeCode.isBlank()) {
                throw new IllegalArgumentException("Os códigos dos nós da restrição não podem conter valores nulos ou vazios.");
            }
            String normalized = nodeCode.trim();
            if (!uniqueNodeCodes.add(normalized)) {
                throw new IllegalArgumentException("Uma restrição não pode referenciar o mesmo nó mais de uma vez.");
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

    public boolean references(String nodeCode) {

        return nodeCodes.contains(nodeCode);
    }

    public boolean sameDefinition(GceRestriction other) {
        return other != null
                && type == other.type
                && nodeCodes.equals(other.nodeCodes);
    }
}
