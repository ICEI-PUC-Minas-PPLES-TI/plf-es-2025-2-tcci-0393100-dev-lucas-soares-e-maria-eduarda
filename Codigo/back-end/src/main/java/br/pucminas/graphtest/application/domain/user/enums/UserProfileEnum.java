package br.pucminas.graphtest.application.domain.user.enums;

import br.pucminas.graphtest.application.exception.InvalidUserProfileException;

/**
 * Enumeração que representa os perfis de acesso de usuários no sistema.
 */
public enum UserProfileEnum {

    ADMIN(1, "ROLE_ADMIN"),
    USUARIO(2, "ROLE_USUARIO");

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

    public static UserProfileEnum getPerfilUsuarioOrThrow(Integer codigo) {
        UserProfileEnum perfil = getPerfilUsuario(codigo);
        if (perfil == null) {
            throw new InvalidUserProfileException("Perfil de usuario invalido: " + codigo);
        }
        return perfil;
    }
}
