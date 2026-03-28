package br.pucminas.graphtest.application.domain;

import br.pucminas.graphtest.application.domain.enums.RestrictionTypeEnum;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa uma restricao formal aplicada a nos de um GCE.
 *
 * <p>Dependendo do tipo, a restricao atua sobre causas ou sobre efeitos e
 * define combinacoes permitidas ou proibidas dentro do modelo.</p>
 */
public class GceRestriction extends BaseEntity {
    private RestrictionTypeEnum type;
    private List<UUID> nodeIds;

    /**
     * Cria uma restricao do GCE.
     *
     * @param id identificador da restricao
     * @param type tipo formal da restricao
     * @param nodeIds nos participantes da restricao
     */
    public GceRestriction(UUID id, RestrictionTypeEnum type, List<UUID> nodeIds) {
        this.id = requireUuid(id);
        this.type = Objects.requireNonNull(type, "type e obrigatorio.");
        this.nodeIds = normalizeNodeIds(nodeIds);

        if (this.nodeIds.isEmpty()) {
            throw new IllegalArgumentException("Uma restricao deve referenciar ao menos um no.");
        }

        if ((type == RestrictionTypeEnum.REQUIRE || type == RestrictionTypeEnum.MASKS) && this.nodeIds.size() != 2) {
            throw new IllegalArgumentException("Restricoes R e M devem possuir exatamente 2 nos.");
        }

        if ((type == RestrictionTypeEnum.EXCLUSIVE || type == RestrictionTypeEnum.INCLUSIVE || type == RestrictionTypeEnum.ONE_AND_ONLY_ONE) && this.nodeIds.size() < 2) {
            throw new IllegalArgumentException("Restricoes E, I e O devem possuir pelo menos 2 nos.");
        }
    }

    /**
     * Garante que o identificador da restricao foi informado.
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
     * Normaliza e valida a lista de nos referenciados pela restricao.
     *
     * @param nodeIds lista recebida
     * @return lista imutavel e sem duplicidades
     */
    private List<UUID> normalizeNodeIds(List<UUID> nodeIds) {
        Objects.requireNonNull(nodeIds, "nodeIds e obrigatorio.");

        LinkedHashSet<UUID> uniqueNodeIds = new LinkedHashSet<>();
        for (UUID nodeId : nodeIds) {
            if (nodeId == null) {
                throw new IllegalArgumentException("nodeIds nao pode conter valores nulos.");
            }
            if (!uniqueNodeIds.add(nodeId)) {
                throw new IllegalArgumentException("Uma restricao nao pode referenciar o mesmo no mais de uma vez.");
            }
        }

        return List.copyOf(new ArrayList<>(uniqueNodeIds));
    }

    /**
     * Retorna o identificador da restricao.
     *
     * @return identificador da restricao
     */
    public UUID getId() {
        return id;
    }

    /**
     * Retorna o primeiro no participante da restricao.
     *
     * @return identificador do primeiro no
     */
    public UUID firstNode() {
        return nodeIds.get(0);
    }

    /**
     * Retorna o segundo no participante da restricao.
     *
     * @return identificador do segundo no
     */
    public UUID secondNode() {
        return nodeIds.get(1);
    }

    /**
     * Retorna o tipo da restricao.
     *
     * @return tipo formal da restricao
     */
    public RestrictionTypeEnum getType() {
        return type;
    }

    /**
     * Retorna os nos participantes da restricao.
     *
     * @return lista imutavel de identificadores
     */
    public List<UUID> getNodeIds() {
        return nodeIds;
    }

    /**
     * Indica se a restricao deve referenciar apenas causas.
     *
     * @return {@code true} quando a restricao for aplicavel a causas
     */
    public boolean isCauseRestriction() {
        return switch (type) {
            case EXCLUSIVE, INCLUSIVE, ONE_AND_ONLY_ONE, REQUIRE -> true;
            case MASKS -> false;
        };
    }

    /**
     * Indica se a restricao deve referenciar apenas efeitos.
     *
     * @return {@code true} quando a restricao for aplicavel a efeitos
     */
    public boolean isEffectRestriction() {
        return type == RestrictionTypeEnum.MASKS;
    }

    /**
     * Verifica se a restricao referencia determinado no.
     *
     * @param nodeId identificador consultado
     * @return {@code true} quando o no participa da restricao
     */
    public boolean references(UUID nodeId) {
        return nodeIds.contains(nodeId);
    }

    /**
     * Compara a definicao desta restricao com outra restricao.
     *
     * @param other outra restricao a ser comparada
     * @return {@code true} quando tipo e conjunto ordenado de nos coincidirem
     */
    public boolean sameDefinition(GceRestriction other) {
        return other != null
                && type == other.type
                && nodeIds.equals(other.nodeIds);
    }
}
