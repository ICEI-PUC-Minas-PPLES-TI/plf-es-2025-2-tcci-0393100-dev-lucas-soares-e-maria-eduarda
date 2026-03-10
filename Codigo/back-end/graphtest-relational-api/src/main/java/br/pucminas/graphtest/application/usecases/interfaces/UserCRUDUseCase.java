package br.pucminas.graphtest.application.usecases.interfaces;

import br.pucminas.graphtest.adapters.inbound.dto.PasswordDTO;
import br.pucminas.graphtest.application.domain.entity.User;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public interface UserCRUDUseCase extends BaseCRUDUseCase<User> {

    void atualizarSenha(@NotNull UUID id, @NotNull PasswordDTO passwordDTO);

    User encontrarPorEmail(@NotNull String email);

    boolean existEmail(String email);
}
