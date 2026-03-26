package br.pucminas.graphtest.application.port.input.project;

import br.pucminas.graphtest.application.port.input.project.records.CreateProjectInput;
import br.pucminas.graphtest.application.port.input.project.records.ProjectOutput;

/**
 * Interface que provê a funcionalidade de criar um novo projeto, recebendo um input com os dados necessários para a criação do projeto e retornando um output com os detalhes do projeto criado.
 */
public interface CreateProjectUseCase {
    ProjectOutput execute(CreateProjectInput input);
}
