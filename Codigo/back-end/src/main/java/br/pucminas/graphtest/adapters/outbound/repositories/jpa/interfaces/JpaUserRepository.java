package br.pucminas.graphtest.adapters.outbound.repositories.jpa.interfaces;

import br.pucminas.graphtest.adapters.outbound.entities.jpa.user.JpaUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaUserRepository extends JpaRepository<JpaUserEntity, UUID> {

    boolean existsByEmail(String email);

    @Transactional(readOnly = true)
    Optional<JpaUserEntity> findByEmail(String email);
}
