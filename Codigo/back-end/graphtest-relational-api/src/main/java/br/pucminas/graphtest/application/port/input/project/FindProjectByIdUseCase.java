package br.pucminas.graphtest.application.port.input.project;

import br.pucminas.graphtest.application.domain.Project;
import br.pucminas.graphtest.application.domain.records.AuthenticatedUser;
import br.pucminas.graphtest.application.port.input.project.records.FindProjectByIdInput;
import br.pucminas.graphtest.application.port.input.project.records.ProjectOutput;

import java.util.UUID;

/**
 * Interface que provê a funcionalidade de encontrar um projeto por seu ID, identificando-o por meio de um input que contém o ID do projeto a ser encontrado e retornando um output com os detalhes do projeto correspondente.
 */
public interface FindProjectByIdUseCase {
    ProjectOutput execute(FindProjectByIdInput input);
}
