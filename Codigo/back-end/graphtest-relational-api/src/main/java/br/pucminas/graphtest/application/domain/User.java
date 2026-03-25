package br.pucminas.graphtest.application.domain;

import br.pucminas.graphtest.application.domain.enums.UserProfileEnum;

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

    private UserProfileEnum profile;

    public User(UUID id, String name, String email, String password, UserProfileEnum profile) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.profile = profile;
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

    public UserProfileEnum getProfile() {
        return profile;
    }

    public void setProfile(UserProfileEnum profile) {
        this.profile = profile;
    }
}
