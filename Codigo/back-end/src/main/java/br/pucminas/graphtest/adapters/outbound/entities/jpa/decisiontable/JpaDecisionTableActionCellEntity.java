package br.pucminas.graphtest.adapters.outbound.entities.jpa.decisiontable;

import br.pucminas.graphtest.adapters.outbound.entities.jpa.JpaBaseEntity;
import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableActionValueEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "TB_DECISION_TABLE_ACTION_CELL")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"decisionTable", "rule", "action"})
public class JpaDecisionTableActionCellEntity extends JpaBaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DECISION_TABLE_ID", nullable = false)
    private JpaDecisionTableEntity decisionTable;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RULE_ID", nullable = false)
    private JpaDecisionTableRuleEntity rule;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ACTION_ID", nullable = false)
    private JpaDecisionTableActionEntity action;

    @Enumerated(EnumType.STRING)
    @Column(name = "VALUE", nullable = false, length = 20)
    private DecisionTableActionValueEnum value;
}
