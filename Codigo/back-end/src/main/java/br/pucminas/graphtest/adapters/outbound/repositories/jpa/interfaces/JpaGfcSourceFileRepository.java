package br.pucminas.graphtest.adapters.outbound.repositories.jpa.interfaces;

import br.pucminas.graphtest.adapters.outbound.entities.jpa.gfc.JpaGfcSourceFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaGfcSourceFileRepository extends JpaRepository<JpaGfcSourceFileEntity, UUID> {

    List<JpaGfcSourceFileEntity> findAllByProject_IdOrderByCreatedAtDesc(UUID projectId);

    void deleteAllByProject_Id(UUID projectId);
}
