package br.pucminas.graphtest.adapters.outbound.repositories.jpa.mapper;

import br.pucminas.graphtest.adapters.outbound.entities.jpa.JpaProjectEntity;
import br.pucminas.graphtest.adapters.outbound.entities.jpa.JpaUserEntity;
import br.pucminas.graphtest.adapters.outbound.repositories.shared.BasePersistenceMapper;
import br.pucminas.graphtest.application.domain.project.model.Project;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Mapper responsavel por converter projetos entre o modelo de dominio e a
 * entidade JPA utilizada na camada de persistencia.
 */
@Component
public class ProjectMapperBase implements BasePersistenceMapper<Project, JpaProjectEntity> {

    /**
     * Converte um projeto de dominio para a entidade JPA correspondente.
     *
     * @param project projeto de dominio a ser convertido
     * @return entidade JPA equivalente ao projeto informado
     */
    @Override
    public JpaProjectEntity toEntity(Project project) {
        if (project == null) {
            return null;
        }

        JpaProjectEntity entity = new JpaProjectEntity();
        entity.setId(project.getId());
        entity.setCreatedAt(project.getCreatedAt());
        entity.setUpdatedAt(project.getUpdatedAt());
        entity.setName(project.getName());
        entity.setDescription(project.getDescription());
        entity.setUser(toUserEntity(project.getUserId()));
        return entity;
    }

    /**
     * Converte uma entidade JPA para o projeto de dominio correspondente.
     *
     * @param entity entidade JPA a ser convertida
     * @return projeto de dominio equivalente a entidade informada
     */
    @Override
    public Project toDomain(JpaProjectEntity entity) {
        if (entity == null) {
            return null;
        }

        Project project = new Project(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                toUserId(entity.getUser()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
        return project;
    }

    /**
     * Cria uma entidade JPA simplificada de usuario a partir de seu
     * identificador, para associacao com o projeto.
     *
     * @param userId identificador do usuario dono do projeto
     * @return entidade de usuario contendo apenas o identificador
     */
    private JpaUserEntity toUserEntity(UUID userId) {
        if (userId == null) {
            return null;
        }

        JpaUserEntity user = new JpaUserEntity();
        user.setId(userId);
        return user;
    }

    /**
     * Extrai o identificador do usuario associado a entidade de projeto.
     *
     * @param user entidade de usuario associada ao projeto
     * @return identificador do usuario ou {@code null} quando ausente
     */
    private UUID toUserId(JpaUserEntity user) {
        return user != null ? user.getId() : null;
    }
}
