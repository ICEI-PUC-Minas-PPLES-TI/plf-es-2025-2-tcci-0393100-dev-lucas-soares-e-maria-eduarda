package br.pucminas.graphtest.service.interfaces;

import br.pucminas.graphtest.dto.PasswordDTO;
import br.pucminas.graphtest.model.User;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public interface UserService extends BaseCRUDService<User> {

    void atualizarSenha(@NotNull UUID id, @NotNull PasswordDTO passwordDTO);

    boolean existEmail(String email);
}
