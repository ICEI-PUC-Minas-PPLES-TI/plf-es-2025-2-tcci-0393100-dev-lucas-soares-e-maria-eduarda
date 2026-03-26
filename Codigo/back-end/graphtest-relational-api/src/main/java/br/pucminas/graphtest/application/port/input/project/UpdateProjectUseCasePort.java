package br.pucminas.graphtest.application.port.input.project;

import br.pucminas.graphtest.application.port.input.project.records.ProjectOutput;
import br.pucminas.graphtest.application.port.input.project.records.UpdateProjectInput;


/**
 * Interface que provê a funcionalidade de atualizar um projeto existente, identificando-o por meio de um input que contém o ID do projeto a ser atualizado e os dados a serem modificados, retornando um output com os detalhes do projeto atualizado.
 */
public interface UpdateProjectUseCasePort {
    ProjectOutput execute(UpdateProjectInput input);
}
