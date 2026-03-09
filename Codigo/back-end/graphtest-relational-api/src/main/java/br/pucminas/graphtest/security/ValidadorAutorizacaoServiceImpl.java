package br.pucminas.graphtest.security;

import br.pucminas.graphtest.application.exception.TopicoNaoEncontradoException;
import br.pucminas.graphtest.application.exception.UsuarioNaoAutorizadoException;
import br.pucminas.graphtest.domain.enums.PerfilUsuario;
import br.pucminas.graphtest.security.interfaces.Validador;
import br.pucminas.graphtest.security.interfaces.ValidadorAutorizacaoRequisicaoService;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static br.pucminas.graphtest.util.ConstantesTopicosUtil.*;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@Slf4j(topic = VALIDADOR_AUTORIZACAO_REQUISICAO_SERVICE)
@Service
public class ValidadorAutorizacaoServiceImpl implements ValidadorAutorizacaoRequisicaoService {

    private final List<Validador> validadores;

    private ValidadorAutorizacaoServiceImpl() {
        this.validadores = new ArrayList<>(asList(
                new UsuarioServiceValidadorImpl()));
    }

    /**
     * Verifica as autorizações de um usuário
     * Essa versão do método suporta que uma requisição seja realizada por usuários não que não possuam perfil ADMIN
     *
     * @param id id do objeto do tipo Usuario relacionado à requisição
     * @return usuário autorizado
     */
    @Override
    public UserDetailsImpl validarAutorizacaoRequisicao(UUID id, String topico) {

        UserDetailsImpl userDetailsImpl = autenticar(); //usuário logado

        boolean usuarioAutorizado = validadores.stream().
                filter(validador -> validador.getTopico().equals(topico))
                .findFirst()
                .orElseThrow(() -> new TopicoNaoEncontradoException(format("tópico não encontrado: %s", topico)))
                .validar(id, userDetailsImpl);

        if (!(usuarioEhAdmin(requireNonNull(userDetailsImpl)) || usuarioAutorizado))
            throw new UsuarioNaoAutorizadoException(format("usuário [%s] não possui autorização para utilizar esse método", userDetailsImpl.getUsername()));

        log.info(format(">>> validarAutorizacaoRequisicao: usuário [%s] autorizado para realizar requisição", userDetailsImpl.getUsername()));
        return userDetailsImpl;
    }

    /**
     * Verifica as autorizações de um usuário
     * Essa versão do método bloqueia qualquer requisição por usuários não ADMINS
     *
     * @return usuário autorizado
     */
    @Override
    public UserDetailsImpl validarAutorizacaoRequisicao() {

        UserDetailsImpl userDetailsImpl = autenticar(); // verifica se tá logado

        if (!usuarioEhAdmin(requireNonNull(userDetailsImpl)))
            throw new UsuarioNaoAutorizadoException(format("usuário [%s] não possui autorização para utilizar esse método", userDetailsImpl.getUsername()));

        log.info(format(">>> validarAutorizacaoRequisicao: usuário [%s] autorizado para realizar requisição", userDetailsImpl.getUsername()));
        return userDetailsImpl;
    }

    /**
     * Retorna o usuário logado
     *
     * @return usuário logado
     */
    @Override
    public UserDetailsImpl getUsuarioLogado() {
        return autenticar();
    }


    /**
     * Autentica usuário
     *
     * @return usuário autenticado, caso contrário null
     */
    private @Nullable UserDetailsImpl autenticar() {
        try {
            return (UserDetailsImpl) getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            throw new UsuarioNaoAutorizadoException("usuário não logado");
        }
    }

    /**
     * Verifica se um usuário é administrador
     *
     * @param userSpringSecurity usuário
     * @return boolean indicando se usuário é administrador ou não
     */
    private boolean usuarioEhAdmin(@NotNull UserDetailsImpl userSpringSecurity) {
        return userSpringSecurity.ehPerfil(PerfilUsuario.ADMIN);
    }
}