package br.pucminas.graphtest.domain;

import java.util.List;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    User findById(UUID id);
    List<User> findAll();
    void deleteById(UUID id);
    User findByEmail(String email);

}
