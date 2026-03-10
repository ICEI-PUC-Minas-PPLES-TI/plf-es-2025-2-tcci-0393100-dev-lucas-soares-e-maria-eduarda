package br.pucminas.graphtest.application.port.output.repositories;

import br.pucminas.graphtest.application.domain.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
    List<User> findAll();
    void deleteById(UUID id);
    User findByEmail(String email);
    String buscarSenhaUsuarioPorId(UUID id);
    void atualizarSenhaUsuario(String senha, UUID id);
}
