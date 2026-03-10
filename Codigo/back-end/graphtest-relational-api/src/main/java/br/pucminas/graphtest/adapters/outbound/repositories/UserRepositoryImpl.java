package br.pucminas.graphtest.adapters.outbound.repositories;

import br.pucminas.graphtest.adapters.outbound.entities.JpaUserEntity;
import br.pucminas.graphtest.adapters.outbound.repositories.interfaces.JpaUserRepository;
import br.pucminas.graphtest.application.domain.entity.User;
import br.pucminas.graphtest.application.port.output.repositories.UserRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    public UserRepositoryImpl(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public User save(User user) {
        JpaUserEntity entity = new JpaUserEntity(user);
        JpaUserEntity savedEntity = jpaUserRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaUserRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public List<User> findAll() {
        return jpaUserRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaUserRepository.deleteById(id);
    }

    @Override
    public User findByEmail(String email) {
        return jpaUserRepository.findByEmail(email)
                .map(this::toDomain)
                .orElse(null);
    }

    @Override
    public String buscarSenhaUsuarioPorId(UUID id) {
        return jpaUserRepository.findById(id)
                .map(JpaUserEntity::getPassword)
                .orElse(null);
    }

    @Override
    public void atualizarSenhaUsuario(String senha, UUID id) {
        jpaUserRepository.findById(id).ifPresent(entity -> {
            entity.setPassword(senha);
            jpaUserRepository.save(entity);
        });
    }



    private User toDomain(JpaUserEntity entity) {
        return new User(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getPerfilUsuario()
        );
    }




}
