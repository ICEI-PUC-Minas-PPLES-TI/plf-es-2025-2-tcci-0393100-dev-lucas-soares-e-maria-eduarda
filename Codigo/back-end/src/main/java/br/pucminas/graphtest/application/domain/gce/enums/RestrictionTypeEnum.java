package br.pucminas.graphtest.application.domain.gce.enums;

import br.pucminas.graphtest.application.exception.InvalidRestrictionTypeException;

public enum RestrictionTypeEnum {

    EXCLUSIVE("E", "AT_MOST_ONE_IS_TRUE"),
    INCLUSIVE("I", "AT_LEAST_ONE_IS_TRUE"),
    ONE_AND_ONLY_ONE("O", "EXACTLY_ONE_IS_TRUE"),
    REQUIRE("R", "A_REQUIRES_B"),
    MASKS("M", "A_MASKS_B");

    private final String codigo;
    private final String descricao;

    RestrictionTypeEnum(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static RestrictionTypeEnum getRestrictionType(String codigo) {
        if (codigo == null) {
            return null;
        }

        for (RestrictionTypeEnum restrictionTypeEnum : RestrictionTypeEnum.values()) {
            if (restrictionTypeEnum.codigo.equals(codigo)) {
                return restrictionTypeEnum;
            }
        }

        return null;
    }

    public static RestrictionTypeEnum getRestrictionTypeOrThrow(String codigo) {
        RestrictionTypeEnum restrictionTypeEnum = getRestrictionType(codigo);
        if (restrictionTypeEnum == null) {
            throw new InvalidRestrictionTypeException("Tipo de restricao inválido: " + codigo);
        }
        return restrictionTypeEnum;
    }
}
