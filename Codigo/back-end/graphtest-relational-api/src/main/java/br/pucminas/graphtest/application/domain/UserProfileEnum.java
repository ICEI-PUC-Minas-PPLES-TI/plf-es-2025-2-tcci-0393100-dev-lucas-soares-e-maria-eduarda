package br.pucminas.graphtest.application.domain;

/**
 * Enumeração que representa os perfis de acesso de usuários no sistema.
 */
public enum UserProfileEnum {

    ADMIN(1, "ROLE_ADMIN"),
    USUARIO(2, "ROLE_USUARIO"),
    TECNICO(3, "ROLE_USUARIO");

    private final Integer codigo;
    private final String descricao;

    UserProfileEnum(Integer codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    /**
     * Obtém o objeto UserProfileEnum relacionado a um código inteiro
     *
     * @param codigo código do perfil
     * @return objeto UserProfileEnum relacionado ao código inteiro
     */
    public static UserProfileEnum getPerfilUsuario(Integer codigo) {
        if (codigo == null) {
            return null;
        }

        for (UserProfileEnum perfil : UserProfileEnum.values()) {
            if (perfil.codigo.equals(codigo)) {
                return perfil;
            }
        }

        return null;
    }
}
