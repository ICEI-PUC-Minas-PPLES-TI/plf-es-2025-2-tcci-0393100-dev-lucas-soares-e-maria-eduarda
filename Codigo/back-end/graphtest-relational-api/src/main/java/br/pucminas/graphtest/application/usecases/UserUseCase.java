package br.pucminas.graphtest.application.usecases;

import br.pucminas.graphtest.dto.PasswordDTO;
import br.pucminas.graphtest.domain.User;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public interface UserUseCase extends BaseCRUDUseCase<User> {

    void atualizarSenha(@NotNull UUID id, @NotNull PasswordDTO passwordDTO);

    User encontrarPorEmail(@NotNull String email);

    boolean existEmail(String email);
}
