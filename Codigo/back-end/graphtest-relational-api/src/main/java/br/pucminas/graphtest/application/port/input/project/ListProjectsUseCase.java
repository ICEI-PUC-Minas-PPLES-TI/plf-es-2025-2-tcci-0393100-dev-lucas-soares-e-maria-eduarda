package br.pucminas.graphtest.application.port.input.project;

import br.pucminas.graphtest.application.port.input.project.records.ProjectOutput;
import java.util.List;

/**
 * Interface que provê a funcionalidade de listar todos os projetos disponíveis, retornando uma lista de outputs com os detalhes de cada projeto registrado no sistema.
 */
public interface ListProjectsUseCase {
    List<ProjectOutput> execute();
}
