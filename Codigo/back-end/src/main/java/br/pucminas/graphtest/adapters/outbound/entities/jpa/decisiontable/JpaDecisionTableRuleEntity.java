package br.pucminas.graphtest.adapters.outbound.entities.jpa.decisiontable;

import br.pucminas.graphtest.adapters.outbound.entities.jpa.shared.JpaAssignedIdEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "TB_DECISION_TABLE_RULE")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = "decisionTable")
public class JpaDecisionTableRuleEntity extends JpaAssignedIdEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DECISION_TABLE_ID", nullable = false)
    private JpaDecisionTableEntity decisionTable;

    @Column(name = "CODE", nullable = false, length = 50)
    private String code;

    @Column(name = "DESCRIPTION", length = 500)
    private String description;

    @Column(name = "ORDER_INDEX", nullable = false)
    private Integer orderIndex;
}
