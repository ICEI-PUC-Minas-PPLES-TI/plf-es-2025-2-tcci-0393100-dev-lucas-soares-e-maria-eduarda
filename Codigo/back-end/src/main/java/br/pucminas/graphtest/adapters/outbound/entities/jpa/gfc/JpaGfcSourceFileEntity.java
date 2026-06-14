package br.pucminas.graphtest.adapters.outbound.entities.jpa.gfc;

import br.pucminas.graphtest.adapters.outbound.entities.jpa.project.JpaProjectEntity;
import br.pucminas.graphtest.adapters.outbound.entities.jpa.shared.JpaAssignedIdEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import java.util.UUID;

@Entity
@Table(name = "TB_GFC_SOURCE_FILE")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class JpaGfcSourceFileEntity extends JpaAssignedIdEntity implements Persistable<UUID> {

    @Transient
    private boolean newEntity = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PROJECT_ID", nullable = false)
    private JpaProjectEntity project;

    @Column(name = "FILE_NAME", nullable = false, length = 255)
    private String fileName;

    @Column(name = "CONTENT", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "LANGUAGE", nullable = false, length = 30)
    private String language;

    @Override
    public boolean isNew() {
        return newEntity;
    }

    public void markAsNew() {
        this.newEntity = true;
    }

    @PostLoad
    @PostPersist
    void markAsPersisted() {
        this.newEntity = false;
    }
}
