package br.pucminas.graphtest.service;

import br.pucminas.graphtest.dto.PasswordDTO;
import br.pucminas.graphtest.exceptions.lancaveis.*;
import br.pucminas.graphtest.model.User;
import br.pucminas.graphtest.model.enums.PerfilUsuario;
import br.pucminas.graphtest.repository.UserRepository;
import br.pucminas.graphtest.security.interfaces.ValidadorAutorizacaoRequisicaoService;
import br.pucminas.graphtest.service.interfaces.UserService;
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

@Slf4j(topic = USUARIO_SERVICE)
@Service
@RequiredArgsConstructor // Gera construtor para aqueles atributos final ou @NonNull
public class UserServiceImpl implements UserService {

    private final UserRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final ValidadorAutorizacaoRequisicaoService validadorAutorizacaoRequisicaoService;


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

    @Override
    public List<User> listarTodos() {
        log.info(">>> listarTodos: listando todos usuários");
        validadorAutorizacaoRequisicaoService.validarAutorizacaoRequisicao();
        return usuarioRepository.findAll();
    }

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

    @Override
    public void atualizarSenha(UUID id, PasswordDTO passwordDTO) {

    }

    @Override
    public User encontrarPorEmail(String email) {
        log.info(">>> encontrarPorEmail: encontrando usuário por email");
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EntidadeNaoEncontradaException(format("usuário não encontrado, email: %s", email)));
    }

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


    @Override
    public boolean existEmail(String email) {
        return false;
    }

}
