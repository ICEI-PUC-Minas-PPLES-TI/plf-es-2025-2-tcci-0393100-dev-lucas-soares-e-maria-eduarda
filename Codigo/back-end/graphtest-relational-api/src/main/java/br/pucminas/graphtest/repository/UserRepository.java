package br.pucminas.graphtest.repository;

import br.pucminas.graphtest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);

    @Transactional(readOnly = true)
    @Query("SELECT u.password FROM User u WHERE u.id = :id")
    String buscarSenhaUsuarioPorId(UUID id);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :senha WHERE u.id = :id")
    void atualizarSenhaUsuario(String senha, UUID id);
}
