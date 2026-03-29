package br.pucminas.graphtest.application.port.input.user;

import br.pucminas.graphtest.application.port.input.user.records.UserOutput;

import java.util.List;

public interface ListUsersUseCasePort {
    List<UserOutput> execute();
}
