package br.pucminas.graphtest.application.service;

import br.pucminas.graphtest.application.exception.DeletarEntidadeException;
import br.pucminas.graphtest.application.exception.EntidadeNaoEncontradaException;
import br.pucminas.graphtest.dto.PasswordDTO;
import br.pucminas.graphtest.domain.User;
import br.pucminas.graphtest.domain.enums.PerfilUsuario;
import br.pucminas.graphtest.adapters.outbound.repository.JpaUserRepository;
import br.pucminas.graphtest.security.interfaces.ValidadorAutorizacaoRequisicaoService;
import br.pucminas.graphtest.application.usecases.UserUseCase;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

import static br.pucminas.graphtest.util.ConstantesRequisicaoUtil.PROPRIEDADES_IGNORADAS;
import static br.pucminas.graphtest.util.ConstantesTopicosUtil.USUARIO_SERVICE;
import static java.lang.String.format;
import static org.springframework.beans.BeanUtils.copyProperties;

/**
 * Regras de negócio do usuário
 */
@Slf4j(topic = USUARIO_SERVICE)
@Service
@RequiredArgsConstructor // Gera construtor para aqueles atributos final ou @NonNull
public class UserUseCaseImpl implements UserUseCase {

    private final JpaUserRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final ValidadorAutorizacaoRequisicaoService validadorAutorizacaoRequisicaoService;

    /**
     * Cria um novo usuário no sistema
     * @param user entidade de usuário a ser criada
     * @return usuário persistido
     */
    @Override
    @Transactional
    public User criar(@NotNull  User user) {
        log.info(">>> criar: criando usuário");
        user.setId(null);
        user.setPassword (passwordEncoder.encode(user.getPassword ()));
        user.setPerfilUsuario(PerfilUsuario.USUARIO.getCodigo());
        user = usuarioRepository.save(user);
        log.info(format(">>> criar: usuário criado, id: %s", user.getId()));
        return user;
    }

    /**
     * Busca um usuário por seu identificador único
     * @param id identificador único do usuário
     * @return usuário encontrado
     */
    @Override
    public User encontrarPorId(@NotNull UUID id) {
        log.info(">>> encontrarPorId: encontrando usuário por id");
        try {
            validadorAutorizacaoRequisicaoService.validarAutorizacaoRequisicao(id, USUARIO_SERVICE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException(format("usuário não encontrado, id: %s", id)));
    }

    /**
     * Retorna todos os usuário cadastrados no sistema
     * @return lista contendo todos os usuários cadastrados
     */
    @Override
    public List<User> listarTodos() {
        log.info(">>> listarTodos: listando todos usuários");
        validadorAutorizacaoRequisicaoService.validarAutorizacaoRequisicao();
        return usuarioRepository.findAll();
    }

    /**
     * Atualiza dados de um usuário existente
     * @param user entidade contendo os novos dados do usuário
     * @return usuário atualizado e persistido
     */
    @Override
    @Transactional
    public User atualizar(@NotNull User user) {
        log.info(">>> atualizar: atualizando usuário");
        User usuarioCadastrado = encontrarPorId(user.getId());
        copyProperties(user, usuarioCadastrado, PROPRIEDADES_IGNORADAS);

        if (usuarioCadastrado.getPerfilUsuario().equals(PerfilUsuario.ADMIN.getCodigo()))
            usuarioCadastrado.setPerfilUsuario(user.getPerfilUsuario());

        return usuarioRepository.save(usuarioCadastrado);
    }

    /**
     * Atualiza a senha do usuário
     * @param id identificador do usuário cuja senha será atualizada
     * @param passwordDTO objeto contendo os dados necessários para alteração de senha
     */
    @Override
    public void atualizarSenha(UUID id, PasswordDTO passwordDTO) {

    }

    /**
     * Busca um usuário a partir do endenreço de e-mail
     *
     * @param email endenreço de e-mail do usuário
     * @return usuário correspondente ao e-mail informado
     * @throws EntidadeNaoEncontradaException caso não exista usuário com o e-mail informado
     */
    @Override
    public User encontrarPorEmail(String email) {
        log.info(">>> encontrarPorEmail: encontrando usuário por email");
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EntidadeNaoEncontradaException(format("usuário não encontrado, email: %s", email)));
    }

    /**
     * Remove um usuário do sistema com base no identificador único informado
     * @param id identificador único do usuário a ser removido
     * @throws DeletarEntidadeException caso a exclusão não possa ser realizada devido à existência de entidades relacionadas
     */
    @Override
    @Transactional
    public void deletar(UUID id) {
        log.info(">>> deletar: deletando usuário");
        User user = encontrarPorId(id);
        try {
            this.usuarioRepository.deleteById(user.getId());
            log.info(format(">>> deletar: usuário deletado, id: %s", id));
        } catch (Exception e) {
            throw new DeletarEntidadeException(format("existem entidades relacionadas: %s", e));
        }
    }

    /**
     * Verifica se existe um usuário com e-mail confirmado
     * @param email endereço de e-mail a ser verificado
     * @return true se existir um usuário com e-mail informado falso caso contrário
     */
    @Override
    public boolean existEmail(String email) {
        return false;
    }

}
