package br.pucminas.graphtest.application.service.user;

import br.pucminas.graphtest.application.exception.DuplicateEmailException;
import br.pucminas.graphtest.application.port.output.repositories.UserRepositoryPort;
import br.pucminas.graphtest.application.service.user.interfaces.UserEmailUniquenessService;

import java.util.UUID;

public class UserEmailUniquenessServiceImpl implements UserEmailUniquenessService {

    private final UserRepositoryPort userRepository;

    public UserEmailUniquenessServiceImpl(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void ensureEmailAvailable(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException("Ja existe um usuario cadastrado com o email informado");
        }
    }

    @Override
    public void ensureEmailAvailableForUpdate(UUID userId, String email) {
        userRepository.findByEmail(email)
                .filter(user -> !user.getId().equals(userId))
                .ifPresent(user -> {
                    throw new DuplicateEmailException("Ja existe um usuario cadastrado com o email informado");
                });
    }
}
