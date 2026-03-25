package br.pucminas.graphtest.adapters.outbound.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TB_PROJECT")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class JpaProjectEntity extends JpaBaseEntity{

    @Column(name = "NAME", length = 50)
    private String name;

    @Column(name = "DESCRIPTION", length = 200)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", nullable = false)
    private JpaUserEntity user;
}
