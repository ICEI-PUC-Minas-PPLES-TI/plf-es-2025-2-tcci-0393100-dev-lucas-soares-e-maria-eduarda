package br.pucminas.graphtest.application.usecases.project;

import br.pucminas.graphtest.application.domain.records.AuthenticatedUser;
import br.pucminas.graphtest.application.port.input.project.ListProjectsByUserUseCase;
import br.pucminas.graphtest.application.port.input.project.records.ProjectOutput;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepository;
import br.pucminas.graphtest.application.port.output.security.CurrentUserPort;

import java.util.List;

/**
 * Caso de uso responsavel por listar os projetos associados ao usuario
 * autenticado.
 */
public class ListProjectsByUserUseCaseImpl implements ListProjectsByUserUseCase {

    private final ProjectRepository projectRepository;
    private final CurrentUserPort currentUserPort;

    /**
     * Cria o caso de uso com as dependencias necessarias para obter o usuario
     * autenticado e consultar seus projetos.
     *
     * @param projectRepository repositorio usado para listar projetos
     * @param currentUserPort porta responsavel por fornecer o usuario autenticado
     */
    public ListProjectsByUserUseCaseImpl(ProjectRepository projectRepository, CurrentUserPort currentUserPort) {
        this.projectRepository = projectRepository;
        this.currentUserPort = currentUserPort;
    }

    /**
     * Lista todos os projetos vinculados ao usuario autenticado e os converte
     * para o formato de saida do caso de uso.
     *
     * @return lista de projetos associados ao usuario autenticado
     */
    @Override
    public List<ProjectOutput> execute() {
        AuthenticatedUser currentUser = currentUserPort.getCurrentUser();

        return projectRepository.findAllByUserId(currentUser.id()).stream()
                .map(ProjectOutput::from)
                .toList();
    }
}
