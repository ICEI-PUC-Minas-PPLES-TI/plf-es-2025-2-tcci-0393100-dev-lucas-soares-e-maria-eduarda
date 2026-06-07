package br.pucminas.graphtest.adapters.outbound.repositories.jpa.interfaces;

import br.pucminas.graphtest.adapters.outbound.entities.jpa.decisiontable.JpaDecisionTableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaDecisionTableRepository extends JpaRepository<JpaDecisionTableEntity, UUID> {

    Optional<JpaDecisionTableEntity> findByGceId(UUID gceId);

    List<JpaDecisionTableEntity> findAllByProject_Id(UUID projectId);

    void deleteByGceId(UUID gceId);

    void deleteAllByProject_Id(UUID projectId);

    boolean existsByGceId(UUID gceId);
}
