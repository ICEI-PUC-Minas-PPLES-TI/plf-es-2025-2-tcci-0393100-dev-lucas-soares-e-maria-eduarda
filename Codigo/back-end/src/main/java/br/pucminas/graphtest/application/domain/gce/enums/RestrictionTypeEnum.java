package br.pucminas.graphtest.application.domain.gce.enums;

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
}
