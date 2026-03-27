package br.pucminas.graphtest.application.port.input.project;

import br.pucminas.graphtest.application.port.input.project.records.ProjectOutput;
import java.util.List;

/**
 * Interface que provê a funcionalidade de listar os projetos associados a um usuário específico, retornando uma lista de outputs com os detalhes dos projetos relacionados ao usuário.
 */
public interface ListProjectsByUserUseCasePort {
    List<ProjectOutput> execute();
}
