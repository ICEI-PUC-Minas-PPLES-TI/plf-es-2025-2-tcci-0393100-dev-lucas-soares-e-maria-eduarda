package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.domain.entity.User;
import br.pucminas.graphtest.application.port.input.user.ListUsersUseCase;
import br.pucminas.graphtest.application.port.output.repositories.UserRepository;
import br.pucminas.graphtest.application.port.security.ValidadorAutorizacaoRequisicao;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListUsersUseCaseImpl implements ListUsersUseCase {

    private final UserRepository userRepository;
    private final ValidadorAutorizacaoRequisicao validadorAutorizacaoRequisicao;

    public ListUsersUseCaseImpl(UserRepository userRepository, ValidadorAutorizacaoRequisicao validadorAutorizacaoRequisicao) {
        this.userRepository = userRepository;
        this.validadorAutorizacaoRequisicao = validadorAutorizacaoRequisicao;
    }

    @Override
    public List<User> execute() {
        validadorAutorizacaoRequisicao.validarAutorizacaoRequisicao();
        return userRepository.findAll();
    }
}
