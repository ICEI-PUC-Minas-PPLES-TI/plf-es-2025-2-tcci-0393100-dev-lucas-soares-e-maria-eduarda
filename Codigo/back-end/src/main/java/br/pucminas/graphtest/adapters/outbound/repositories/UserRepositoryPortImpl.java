package br.pucminas.graphtest.adapters.outbound.repositories;

import br.pucminas.graphtest.adapters.outbound.entities.JpaUserEntity;
import br.pucminas.graphtest.adapters.outbound.repositories.interfaces.jpa.JpaUserRepository;
import br.pucminas.graphtest.adapters.outbound.repositories.mappers.PersistenceMapper;
import br.pucminas.graphtest.application.domain.User;
import br.pucminas.graphtest.application.port.output.repositories.UserRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador de saida responsavel por implementar as operacoes de persistencia
 * de usuarios definidas pela porta {@link UserRepositoryPort}.
 */
@Repository
public class UserRepositoryPortImpl implements UserRepositoryPort {

    private final JpaUserRepository jpaUserRepository;
    private final PersistenceMapper<User, JpaUserEntity> mapper;

    /**
     * Cria o repositorio com as dependencias necessarias para acessar o banco
     * de dados e converter entidades entre persistencia e dominio.
     *
     * @param jpaUserRepository repositorio JPA usado nas operacoes de banco
     * @param mapper mapper usado nas conversoes entre dominio e entidade
     */
    public UserRepositoryPortImpl(JpaUserRepository jpaUserRepository, PersistenceMapper<User, JpaUserEntity> mapper) {
        this.jpaUserRepository = jpaUserRepository;
        this.mapper = mapper;
    }

    /**
     * Persiste um usuario no banco de dados.
     *
     * @param user usuario a ser persistido
     * @return usuario salvo ja convertido para o modelo de dominio
     */
    @Override
    public User save(User user) {
        JpaUserEntity entity = mapper.toEntity(user);
        JpaUserEntity savedEntity = jpaUserRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    /**
     * Busca um usuario pelo identificador.
     *
     * @param id identificador do usuario
     * @return usuario encontrado, quando existente
     */
    @Override
    public Optional<User> findById(UUID id) {
        return jpaUserRepository.findById(id)
                .map(mapper::toDomain);
    }

    /**
     * Lista todos os usuarios persistidos.
     *
     * @return lista de usuarios convertidos para o modelo de dominio
     */
    @Override
    public List<User> findAll() {
        return jpaUserRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Remove um usuario pelo identificador.
     *
     * @param id identificador do usuario a ser removido
     */
    @Override
    public void deleteById(UUID id) {
        jpaUserRepository.deleteById(id);
    }

    /**
     * Busca um usuario pelo endereco de email.
     *
     * @param email email do usuario
     * @return usuario encontrado, quando existente
     */
    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }
}
