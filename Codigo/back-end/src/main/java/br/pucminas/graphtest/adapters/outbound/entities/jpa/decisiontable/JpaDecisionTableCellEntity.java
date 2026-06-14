package br.pucminas.graphtest.adapters.outbound.entities.jpa.decisiontable;

import br.pucminas.graphtest.adapters.outbound.entities.jpa.shared.JpaAssignedIdEntity;
import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableCellValueEnum;
import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableElementEnum;
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
@Table(name = "TB_DECISION_TABLE_CELL")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"decisionTable", "ruleElement", "decisionTableElement"})
public class JpaDecisionTableCellEntity extends JpaAssignedIdEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DECISION_TABLE_ID", nullable = false)
    private JpaDecisionTableEntity decisionTable;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RULE_ELEMENT_ID", nullable = false)
    private JpaDecisionTableElementEntity ruleElement;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DECISION_TABLE_ELEMENT_ID", nullable = false)
    private JpaDecisionTableElementEntity decisionTableElement;

    @Enumerated(EnumType.STRING)
    @Column(name = "CELL_TYPE", nullable = false, length = 30)
    private DecisionTableElementEnum type;

    @Enumerated(EnumType.STRING)
    @Column(name = "CELL_VALUE", nullable = false, length = 20)
    private DecisionTableCellValueEnum value;
}
