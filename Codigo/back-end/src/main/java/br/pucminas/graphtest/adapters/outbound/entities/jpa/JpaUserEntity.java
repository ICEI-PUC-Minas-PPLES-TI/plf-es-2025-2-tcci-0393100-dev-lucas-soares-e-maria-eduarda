package br.pucminas.graphtest.adapters.outbound.entities.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Entidade que representa um usuario do sistema.
 */
@Entity
@Table(name = "TB_USER")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class JpaUserEntity extends JpaBaseEntity {

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "EMAIL", unique = true, nullable = false)
    private String email;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "PERFIL_USUARIO", nullable = false)
    private Integer perfilUsuario;
}
