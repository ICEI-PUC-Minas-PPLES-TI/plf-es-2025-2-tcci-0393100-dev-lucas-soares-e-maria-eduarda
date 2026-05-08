package br.pucminas.graphtest.adapters.outbound.entities.jpa.decisiontable;

import br.pucminas.graphtest.adapters.outbound.entities.jpa.shared.JpaAssignedIdEntity;
import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableConditionValueEnum;
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
@Table(name = "TB_DECISION_TABLE_CONDITION_CELL")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"decisionTable", "rule", "condition"})
public class JpaDecisionTableConditionCellEntity extends JpaAssignedIdEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DECISION_TABLE_ID", nullable = false)
    private JpaDecisionTableEntity decisionTable;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RULE_ID", nullable = false)
    private JpaDecisionTableRuleEntity rule;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CONDITION_ID", nullable = false)
    private JpaDecisionTableConditionEntity condition;

    @Enumerated(EnumType.STRING)
    @Column(name = "VALUE", nullable = false, length = 20)
    private DecisionTableConditionValueEnum value;
}
