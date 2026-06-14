package br.pucminas.graphtest.application.port.input.project;

import br.pucminas.graphtest.application.port.input.project.records.DeleteProjectInput;

/**
 * Interface que provê a funcionalidade de deletar um projeto específico, identificando-o por meio de um input que contém o ID do projeto a ser removido.
 */
public interface DeleteProjectUseCasePort {
    void execute(DeleteProjectInput input);
}
