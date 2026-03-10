package br.pucminas.graphtest.application.usecases.security;

import br.pucminas.graphtest.application.port.security.Validador;
import br.pucminas.graphtest.adapters.outbound.security.UserDetailsImpl;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import java.util.UUID;
import static br.pucminas.graphtest.infrastructure.util.ConstantesTopicosUtil.USUARIO_SERVICE;

@AllArgsConstructor
public class UsuarioValidadorImpl implements Validador {

    /**
     * Valida um usuário para efetuar uma requisição
     *
     * @param idUsuario     id do usuário relativo à requisição
     * @param userDetailsImpl objeto do tipo usuarioDetails
     * @return boolean indicando se o usuário foi ou não validado
     */
    @Override
    public boolean validar(UUID idUsuario, @NotNull UserDetailsImpl userDetailsImpl) {
        return userDetailsImpl.getId().equals(idUsuario);
    }

    /**
     * Obtém o tópico do validador
     *
     * @return tópico
     */
    @Override
    public String getTopico() {
        return USUARIO_SERVICE;
    }
}
