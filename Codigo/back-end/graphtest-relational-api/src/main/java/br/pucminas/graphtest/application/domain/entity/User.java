package br.pucminas.graphtest.application.domain.entity;


import java.util.UUID;

/**
 * Entidade que representa um usuário do sistema.
 *
 * @author lucas S.
 * @since 1.0
 */
public class User extends BaseEntity {

    private String name;

    private String email;

    private String password;

    private Integer perfilUsuario;

    public User(UUID id, String name, String email, String password, Integer perfilUsuario) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.perfilUsuario = perfilUsuario;
    }

    public User() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getPerfilUsuario() {
        return perfilUsuario;
    }

    public void setPerfilUsuario(Integer perfilUsuario) {
        this.perfilUsuario = perfilUsuario;
    }
}
