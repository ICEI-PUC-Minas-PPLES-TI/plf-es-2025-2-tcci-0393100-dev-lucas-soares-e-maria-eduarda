package br.pucminas.graphtest.adapters.outbound.entities;

import br.pucminas.graphtest.domain.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import static br.pucminas.graphtest.util.ConstantesErroValidadorUtil.*;

/**
 * Entidade que representa um usuário do sistema.
 *
 * @author lucas S.
 * @since 1.0
 */
@Entity
@Table(name = "TB_USER")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class JpaUserEntity extends JpaBaseEntity {

    @Column(name = "NAME", length = 50, nullable = false)
    private String name;

    @Column(name = "EMAIL", unique = true, nullable = false)
    @Pattern(regexp = "^[a-z0-9.]+@[a-z0-9]+\\.[a-z]+\\.?([a-z]+)?$", message = MSG_ERRO_EMAIL)
    private String email;

    @Column(name = "PASSWORD", nullable = false)
    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 8, max = 100, message = MSG_ERRO_SENHA)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name = "PERFIL_USUARIO", nullable = false)
    @JsonProperty("perfil_usuario")
    private Integer perfilUsuario;

    public JpaUserEntity(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.perfilUsuario = user.getPerfilUsuario();
    }
}

