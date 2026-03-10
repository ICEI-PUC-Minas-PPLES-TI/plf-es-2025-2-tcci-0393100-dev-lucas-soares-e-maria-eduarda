package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.domain.entity.User;
import br.pucminas.graphtest.application.domain.enums.UserProfileEnum;
import br.pucminas.graphtest.application.port.input.user.UpdateUserUseCase;
import br.pucminas.graphtest.application.port.input.user.command.UpdateUserCommand;
import br.pucminas.graphtest.application.port.output.repositories.UserRepository;
import org.springframework.stereotype.Service;
import static br.pucminas.graphtest.adapters.inbound.util.ConstantesRequisicaoUtil.PROPRIEDADES_IGNORADAS;
import static org.springframework.beans.BeanUtils.copyProperties;

@Service
public class UpdateUserUseCaseImpl implements UpdateUserUseCase {

    private final UserRepository userRepository;

    public UpdateUserUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User execute(UpdateUserCommand command) {

        User usuarioCadastrado = userRepository.findById(command.id())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuarioCadastrado.setName(command.name());
        usuarioCadastrado.setEmail(command.email());

        if (usuarioCadastrado.getPerfilUsuario().equals(UserProfileEnum.USUARIO.getCodigo())) {
            usuarioCadastrado.setPerfilUsuario(command.perfilUsuario());
        }

        return userRepository.save(usuarioCadastrado);
    }
}
