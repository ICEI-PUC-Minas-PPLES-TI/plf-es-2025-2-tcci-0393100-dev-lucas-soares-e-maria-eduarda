package br.pucminas.graphtest.application.port.output.repositories;

import br.pucminas.graphtest.application.domain.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
    List<User> findAll();
    void deleteById(UUID id);
    Optional<User> findByEmail(String email);
}
