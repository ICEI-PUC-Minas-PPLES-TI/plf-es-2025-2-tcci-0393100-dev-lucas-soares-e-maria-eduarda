package br.pucminas.graphtest.adapters.inbound.security;

import br.pucminas.graphtest.application.port.input.security.LoadAuthenticationUserUseCase;
import br.pucminas.graphtest.application.port.input.security.records.LoadAuthenticationUserInput;
import br.pucminas.graphtest.application.port.input.security.records.AuthenticationUserResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static br.pucminas.graphtest.shared.logging.LogTopics.USUARIO_SPRING_SECURITY_SERVICE;

@Slf4j(topic = USUARIO_SPRING_SECURITY_SERVICE)
@Service
@AllArgsConstructor
public class CustomUserDetailsAdapter implements UserDetailsService {

    private final LoadAuthenticationUserUseCase loadAuthenticationUserUseCase;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AuthenticationUserResult usuario;
        try {
            usuario = loadAuthenticationUserUseCase.execute(new LoadAuthenticationUserInput(email));
        } catch (RuntimeException exception) {
            throw new UsernameNotFoundException("Usuario nao encontrado: " + email, exception);
        }

        return UserDetailsAdapter.builder()
                .id(usuario.id())
                .email(usuario.email())
                .senha(usuario.password())
                .perfilUsuario(usuario.profile())
                .build();
    }
}
