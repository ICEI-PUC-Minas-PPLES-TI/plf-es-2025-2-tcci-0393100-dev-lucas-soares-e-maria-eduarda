package br.pucminas.graphtest.security;

import br.pucminas.graphtest.domain.User;
import br.pucminas.graphtest.domain.enums.PerfilUsuario;
import br.pucminas.graphtest.adapters.outbound.repository.JpaUserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import static br.pucminas.graphtest.util.ConstantesTopicosUtil.USUARIO_SPRING_SECURITY_SERVICE;

/**
 * Classe responsável por carregar os dados do usuário para autenticação
 */
@Slf4j(topic = USUARIO_SPRING_SECURITY_SERVICE)
@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private JpaUserRepository jpaUserRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User usuario = jpaUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        return UserDetailsImpl.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .senha(usuario.getPassword())
                .perfilUsuario(PerfilUsuario.getPerfilUsuario(usuario.getPerfilUsuario()))
                .build();
    }
}
