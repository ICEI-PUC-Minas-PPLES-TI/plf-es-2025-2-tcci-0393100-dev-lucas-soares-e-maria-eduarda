package br.pucminas.graphtest.adapters.outbound.entities.jpa.decisiontable;

import br.pucminas.graphtest.adapters.outbound.entities.jpa.project.JpaProjectEntity;
import br.pucminas.graphtest.adapters.outbound.entities.jpa.shared.JpaAssignedIdEntity;
import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableSyncStatusEnum;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "TB_DECISION_TABLE")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"elements", "cells"})
public class JpaDecisionTableEntity extends JpaAssignedIdEntity implements Persistable<UUID> {

    /**
     * O agregado de tabela de decisao e montado em memoria com UUIDs proprios antes da persistencia
     * para que regras e celulas possam se referenciar internamente. Sem um controle explicito de
     * "isNew", o Spring Data interpretaria uma tabela nova com ID preenchido como entidade
     * existente e acionaria {@code merge}, o que falha na primeira geracao.
     */
    @Transient
    private boolean newEntity = true;

    @Column(name = "GCE_ID")
    private UUID gceId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PROJECT_ID", nullable = false)
    private JpaProjectEntity project;

    @Column(name = "NAME", nullable = false, length = 200)
    private String name;

    @Column(name = "DESCRIPTION", length = 500)
    private String description;

    @Column(name = "SOURCE_FINGERPRINT", nullable = false, length = 64)
    private String sourceFingerprint;

    @Enumerated(EnumType.STRING)
    @Column(name = "SYNC_STATUS", nullable = false, length = 30)
    private DecisionTableSyncStatusEnum syncStatus;

    @Column(name = "SOURCE_GCE_UPDATED_AT")
    private LocalDateTime sourceGceUpdatedAt;

    @OneToMany(mappedBy = "decisionTable", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JpaDecisionTableElementEntity> elements = new ArrayList<>();

    @OneToMany(mappedBy = "decisionTable", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JpaDecisionTableCellEntity> cells = new ArrayList<>();

    @Override
    public boolean isNew() {
        return newEntity;
    }

    /**
     * Marca explicitamente a entidade como nova para que o Spring Data use {@code persist}
     * mesmo quando o ID ja foi atribuido pela camada de dominio.
     */
    public void markAsNew() {
        this.newEntity = true;
    }

    /**
     * Marca explicitamente a entidade como existente para que o Spring Data use {@code merge}
     * nas regeneracoes e atualizacoes da tabela.
     */
    public void markAsExisting() {
        this.newEntity = false;
    }

    @PostLoad
    @PostPersist
    void markAsPersisted() {
        this.newEntity = false;
    }
}
