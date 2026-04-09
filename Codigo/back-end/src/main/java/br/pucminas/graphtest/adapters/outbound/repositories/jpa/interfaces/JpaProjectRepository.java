package br.pucminas.graphtest.adapters.outbound.repositories.jpa.interfaces;

import br.pucminas.graphtest.adapters.outbound.entities.jpa.JpaProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaProjectRepository extends JpaRepository<JpaProjectEntity, UUID> {

    Optional<JpaProjectEntity> findByIdAndUserId(UUID id, UUID userId);

    List<JpaProjectEntity> findAllByUserId(UUID userId);

    long countByUserId(UUID userId);

    void deleteAllByUser_Id(UUID userId);
}
