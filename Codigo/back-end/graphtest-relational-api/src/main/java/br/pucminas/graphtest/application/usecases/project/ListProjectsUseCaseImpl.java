package br.pucminas.graphtest.application.usecases.project;

import br.pucminas.graphtest.application.domain.records.AuthenticatedUser;
import br.pucminas.graphtest.application.exception.UnauthorizedUserException;
import br.pucminas.graphtest.application.port.input.project.ListProjectsUseCase;
import br.pucminas.graphtest.application.port.input.project.records.ProjectOutput;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepository;
import br.pucminas.graphtest.application.port.output.security.CurrentUserPort;
import java.util.List;

/**
 * Caso de uso responsavel por listar todos os projetos cadastrados no sistema.
 * Essa operacao é restrita a usuarios com perfil de administrador.
 */
public class ListProjectsUseCaseImpl implements ListProjectsUseCase {

    private final ProjectRepository projectRepository;
    private final CurrentUserPort currentUserPort;

    /**
     * Cria o caso de uso com as dependencias necessarias para obter o usuario
     * autenticado e consultar os projetos persistidos.
     *
     * @param projectRepository repositorio usado para listar todos os projetos
     * @param currentUserPort porta responsavel por fornecer o usuario autenticado
     */
    public ListProjectsUseCaseImpl(ProjectRepository projectRepository,
                                   CurrentUserPort currentUserPort) {
        this.projectRepository = projectRepository;
        this.currentUserPort = currentUserPort;
    }

    /**
     * Lista todos os projetos cadastrados, desde que o usuario autenticado seja
     * administrador.
     *
     * @return lista com todos os projetos do sistema
     * @throws UnauthorizedUserException quando o usuario autenticado nao possui
     * permissao para executar a operacao
     */
    @Override
    public List<ProjectOutput> execute() {
        AuthenticatedUser currentUser = currentUserPort.getCurrentUser();

        if (!currentUser.isAdmin()) {
            throw new UnauthorizedUserException("Somente administradores podem listar todos os projetos");
        }

        return projectRepository.findAll()
                .stream()
                .map(ProjectOutput::from)
                .toList();
    }
}
