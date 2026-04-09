package br.pucminas.graphtest.adapters.outbound.repositories.jpa.mapper;

import br.pucminas.graphtest.adapters.outbound.entities.jpa.JpaUserEntity;
import br.pucminas.graphtest.adapters.outbound.repositories.shared.BasePersistenceMapper;
import br.pucminas.graphtest.application.domain.user.model.User;
import br.pucminas.graphtest.application.domain.user.enums.UserProfileEnum;
import org.springframework.stereotype.Component;

/**
 * Mapper responsavel por converter usuarios entre o modelo de dominio e a
 * entidade JPA utilizada na persistencia.
 */
@Component
public class UserMapperBase implements BasePersistenceMapper<User, JpaUserEntity> {

    /**
     * Converte um usuario de dominio para a entidade JPA correspondente.
     *
     * @param user usuario de dominio a ser convertido
     * @return entidade JPA equivalente ao usuario informado
     */
    @Override
    public JpaUserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }

        JpaUserEntity entity = new JpaUserEntity();
        entity.setId(user.getId());
        entity.setName(user.getName());
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPassword());
        entity.setPerfilUsuario(toProfileCode(user.getProfile()));
        return entity;
    }

    /**
     * Converte uma entidade JPA para o usuario de dominio correspondente.
     *
     * @param entity entidade JPA a ser convertida
     * @return usuario de dominio equivalente a entidade informada
     */
    @Override
    public User toDomain(JpaUserEntity entity) {
        if (entity == null) {
            return null;
        }

        return new User(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPassword(),
                toProfile(entity.getPerfilUsuario())
        );
    }

    /**
     * Converte o perfil de dominio para o codigo inteiro persistido em banco.
     *
     * @param profile perfil do usuario no dominio
     * @return codigo numerico do perfil ou {@code null} quando ausente
     */
    private Integer toProfileCode(UserProfileEnum profile) {
        return profile != null ? profile.getCodigo() : null;
    }

    /**
     * Converte o codigo persistido em banco para o perfil de dominio.
     *
     * @param profileCode codigo numerico do perfil do usuario
     * @return perfil correspondente no dominio ou {@code null} quando ausente
     */
    private UserProfileEnum toProfile(Integer profileCode) {
        return UserProfileEnum.getPerfilUsuario(profileCode);
    }
}
