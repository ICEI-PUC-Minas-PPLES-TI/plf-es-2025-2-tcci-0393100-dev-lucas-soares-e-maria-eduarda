package br.pucminas.graphtest.service;

import br.pucminas.graphtest.dto.PasswordDTO;
import br.pucminas.graphtest.exceptions.lancaveis.*;
import br.pucminas.graphtest.model.User;
import br.pucminas.graphtest.repository.UserRepository;
import br.pucminas.graphtest.service.interfaces.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import static br.pucminas.graphtest.util.ConstantesTopicosUtil.USUARIO_SERVICE;
import static java.lang.String.format;

@Slf4j(topic = USUARIO_SERVICE)
@Service
@RequiredArgsConstructor // Gera construtor para aqueles atributos final ou @NonNull
public class UserServiceImpl implements UserService {

    private final UserRepository usuarioRepository;


    @Override
    @Transactional
    public User criar(@NotNull  User user) {
        log.info(">>> criar: criando usuário");
        user.setId(null);
        user = usuarioRepository.save(user);
        log.info(format(">>> criar: usuário criado, id: %s", user.getId()));
        return user;
    }

    @Override
    public User encontrarPorId(@NotNull UUID id) {
        log.info(">>> encontrarPorId: encontrando usuário por id");
        try {
            //validadorAutorizacaoRequisicaoService.validarAutorizacaoRequisicao(id, USUARIO_SERVICE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException(format("usuário não encontrado, id: %s", id)));
    }

    @Override
    public List<User> listarTodos() {
        return List.of();
    }

    @Override
    public User atualizar(User obj) {
        return null;
    }

    @Override
    public void atualizarSenha(UUID id, PasswordDTO passwordDTO) {

    }

    @Override
    public void deletar(UUID id) {

    }


    @Override
    public boolean existEmail(String email) {
        return false;
    }

}
