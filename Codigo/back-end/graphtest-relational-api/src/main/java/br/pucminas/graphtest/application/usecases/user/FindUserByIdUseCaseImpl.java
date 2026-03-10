package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.domain.entity.User;
import br.pucminas.graphtest.application.exception.EntidadeNaoEncontradaException;
import br.pucminas.graphtest.application.port.input.user.FindUserByIdUseCase;
import br.pucminas.graphtest.application.port.output.repositories.UserRepository;
import br.pucminas.graphtest.application.port.security.ValidadorAutorizacaoRequisicao;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;
import static br.pucminas.graphtest.infrastructure.util.ConstantesTopicosUtil.USUARIO_SERVICE;
import static java.lang.String.format;

@Service
public class FindUserByIdUseCaseImpl implements FindUserByIdUseCase {

    private final UserRepository userRepository;
    private final ValidadorAutorizacaoRequisicao validadorAutorizacaoRequisicao;

    public FindUserByIdUseCaseImpl(UserRepository userRepository, ValidadorAutorizacaoRequisicao validadorAutorizacaoRequisicao) {
        this.userRepository = userRepository;
        this.validadorAutorizacaoRequisicao = validadorAutorizacaoRequisicao;
    }

    @Override
    public User execute(UUID id) {
        try {
            validadorAutorizacaoRequisicao.validarAutorizacaoRequisicao(id, USUARIO_SERVICE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException(format("usuário não encontrado, id: %s", id)));
    }
}
