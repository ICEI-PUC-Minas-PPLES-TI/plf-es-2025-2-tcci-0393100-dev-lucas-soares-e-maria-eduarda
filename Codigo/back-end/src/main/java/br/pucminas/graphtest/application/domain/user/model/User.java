package br.pucminas.graphtest.application.domain.user.model;

import br.pucminas.graphtest.application.domain.shared.model.BaseEntity;
import br.pucminas.graphtest.application.domain.user.enums.UserProfileEnum;

import java.time.LocalDateTime;
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
        this(id, name, email, password, profile, null, null);
    }

    public User(UUID id,
                String name,
                String email,
                String password,
                UserProfileEnum profile,
                LocalDateTime createdAt,
                LocalDateTime updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.name = name;
        this.email = email;
        this.password = password;
        this.profile = profile;
    }

    public User() {
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
