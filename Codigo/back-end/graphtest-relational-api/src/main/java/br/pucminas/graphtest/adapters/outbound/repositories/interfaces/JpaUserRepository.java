package br.pucminas.graphtest.adapters.outbound.repositories.interfaces;

import br.pucminas.graphtest.adapters.outbound.entities.JpaUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaUserRepository extends JpaRepository<JpaUserEntity, UUID> {


    boolean existsByEmail(String email);

    @Transactional(readOnly = true)
    Optional<JpaUserEntity> findByEmail(String email);

    @Transactional(readOnly = true)
    @Query("SELECT u.password FROM JpaUserEntity u WHERE u.id = :id")
    String buscarSenhaUsuarioPorId(UUID id);

    @Modifying
    @Transactional
    @Query("UPDATE JpaUserEntity u SET u.password = :senha WHERE u.id = :id")
    void atualizarSenhaUsuario(String senha, UUID id);
}
