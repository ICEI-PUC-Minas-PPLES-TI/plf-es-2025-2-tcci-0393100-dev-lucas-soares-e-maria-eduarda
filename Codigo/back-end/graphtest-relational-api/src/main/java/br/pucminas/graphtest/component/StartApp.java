package br.pucminas.graphtest.component;

import br.pucminas.graphtest.model.User;
import br.pucminas.graphtest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartApp implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        String email = "mariaeduarda@email.com";

        if (!userRepository.existsByEmail(email)) {
            User user = new User();
            user.setName("Maria Eduarda");
            user.setEmail(email);
            user.setPassword("mariaeduarda");

            userRepository.save(user);
            System.out.println("Usuário cadastrado com sucesso.");
        } else {
            System.out.println("Usuário já existe no banco.");
        }

        for (User u : userRepository.findAll()) {
            System.out.println(u);
        }
    }
}