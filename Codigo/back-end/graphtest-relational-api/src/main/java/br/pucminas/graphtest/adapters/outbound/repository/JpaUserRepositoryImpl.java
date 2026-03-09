package br.pucminas.graphtest.adapters.outbound.repository;

import br.pucminas.graphtest.adapters.outbound.entities.JpaUserEntity;
import br.pucminas.graphtest.domain.User;
import br.pucminas.graphtest.domain.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class JpaUserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    public JpaUserRepositoryImpl(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public User save(User user) {
        JpaUserEntity userEntity = new JpaUserEntity(user);
        this.jpaUserRepository.save(userEntity);
        return new User(userEntity.getId(), userEntity.getName(), userEntity.getEmail(), userEntity.getPassword(), userEntity.getPerfilUsuario())
    }

    @Override
    public User findById(UUID id) {
        Optional<JpaUserEntity> userEntity = this.jpaUserRepository.findById(id);
        return convertEntity(userEntity);
    }

    @Override
    public List<User> findAll() {
        return this.jpaUserRepository
                .findAll()
                .stream()
                .map(entity -> new User(entity.getId(), entity.getName(), entity.getEmail(), entity.getPassword(), entity.getPerfilUsuario()))
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        this.jpaUserRepository.deleteById(id);
    }

    @Override
    public User findByEmail(String email) {
        Optional<JpaUserEntity> userEntity = this.jpaUserRepository.findByEmail(email);
        return convertEntity(userEntity);
    }


    private User convertEntity(Optional<JpaUserEntity> jpaUserEntity) {
        return jpaUserEntity.map(entity -> new User(entity.getId(), entity.getName(), entity.getEmail(), entity.getPassword(), entity.getPerfilUsuario()))
                .orElse(null);
    }
}
