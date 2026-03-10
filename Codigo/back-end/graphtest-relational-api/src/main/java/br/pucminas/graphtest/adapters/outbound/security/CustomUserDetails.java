package br.pucminas.graphtest.adapters.outbound.security;

import br.pucminas.graphtest.adapters.outbound.entities.JpaUserEntity;
import br.pucminas.graphtest.application.domain.entity.User;
import br.pucminas.graphtest.adapters.outbound.repositories.interfaces.JpaUserRepository;
import br.pucminas.graphtest.application.domain.enums.UserProfileEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

import static br.pucminas.graphtest.infrastructure.util.ConstantesTopicosUtil.USUARIO_SPRING_SECURITY_SERVICE;

/**
 * Classe responsável por carregar os dados do usuário para autenticação
 */
@Slf4j(topic = USUARIO_SPRING_SECURITY_SERVICE)
@Service
@AllArgsConstructor
public class CustomUserDetails implements UserDetailsService {

    private JpaUserRepository jpaUserRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {

            User usuario = toDomain(jpaUserRepository.findByEmail(email).get());

            return UserDetailsImpl.builder()
                    .id(usuario.getId())
                    .email(usuario.getEmail())
                    .senha(usuario.getPassword())
                    .perfilUsuario(UserProfileEnum.getPerfilUsuario(usuario.getPerfilUsuario()))
                    .build();
        } catch (NoSuchElementException e) {

            throw new UsernameNotFoundException("Usuário não encontrado: " + email);
        }
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
