package br.pucminas.graphtest.adapters.outbound.repositories;

import br.pucminas.graphtest.adapters.outbound.entities.JpaProjectEntity;
import br.pucminas.graphtest.adapters.outbound.repositories.interfaces.JpaProjectRepository;
import br.pucminas.graphtest.adapters.outbound.repositories.mappers.PersistenceMapper;
import br.pucminas.graphtest.application.domain.Project;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador de saida responsavel por implementar as operacoes de persistencia
 * de projetos definidas pela porta {@link ProjectRepository}.
 */
@Repository
@AllArgsConstructor
public class ProjectRepositoryImpl implements ProjectRepository {

    private final JpaProjectRepository jpaProjectRepository;
    private final PersistenceMapper<Project, JpaProjectEntity> mapper;

    /**
     * Persiste um projeto no banco de dados.
     *
     * @param project projeto a ser persistido
     * @return projeto salvo ja convertido para o modelo de dominio
     */
    @Override
    public Project save(Project project) {
        return mapper.toDomain(jpaProjectRepository.save(mapper.toEntity(project)));
    }

    /**
     * Busca um projeto pelo identificador.
     *
     * @param id identificador do projeto
     * @return projeto encontrado, quando existente
     */
    @Override
    public Optional<Project> findById(UUID id) {
        return jpaProjectRepository.findById(id)
                .map(mapper::toDomain);
    }

    /**
     * Lista todos os projetos persistidos.
     *
     * @return lista de projetos convertidos para o modelo de dominio
     */
    @Override
    public List<Project> findAll() {
        return jpaProjectRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Lista todos os projetos associados a um usuario especifico.
     *
     * @param userId identificador do usuario dono dos projetos
     * @return lista de projetos vinculados ao usuario informado
     */
    @Override
    public List<Project> findAllByUserId(UUID userId) {
        return jpaProjectRepository.findAllByUserId(userId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Busca um projeto pelo identificador, restringindo a consulta a um usuario
     * especifico.
     *
     * @param id identificador do projeto
     * @param userId identificador do usuario dono do projeto
     * @return projeto encontrado, quando existir e pertencer ao usuario
     */
    @Override
    public Optional<Project> findByIdAndUserId(UUID id, UUID userId) {
        return jpaProjectRepository.findByIdAndUserId(id, userId)
                .map(mapper::toDomain);
    }

    /**
     * Remove um projeto pelo identificador.
     *
     * @param id identificador do projeto a ser removido
     */
    @Override
    public void deleteById(UUID id) {
        jpaProjectRepository.deleteById(id);
    }

    /**
     * Conta quantos projetos estao associados a um usuario.
     *
     * @param userId identificador do usuario
     * @return quantidade de projetos vinculados ao usuario informado
     */
    @Override
    public long countByUserId(UUID userId) {
        return jpaProjectRepository.countByUserId(userId);
    }

    @Override
    public void deleteAllByUserId(UUID userId) {
        jpaProjectRepository.deleteAllByUser_Id(userId);
    }
}
