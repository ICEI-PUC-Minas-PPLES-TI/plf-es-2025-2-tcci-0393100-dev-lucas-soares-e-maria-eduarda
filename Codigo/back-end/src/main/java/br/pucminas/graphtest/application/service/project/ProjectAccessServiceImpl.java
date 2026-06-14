package br.pucminas.graphtest.application.service.project;

import br.pucminas.graphtest.application.domain.project.model.Project;
import br.pucminas.graphtest.application.security.AuthenticatedUser;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.exception.UnauthorizedUserException;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepositoryPort;
import br.pucminas.graphtest.application.port.output.security.CurrentUserPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;
import java.util.UUID;

/**
 * Servico responsavel por localizar projetos visiveis para o usuario autenticado.
 * Administradores podem acessar qualquer projeto, enquanto os demais usuarios
 * somente podem acessar projetos associados ao proprio identificador.
 */
public class ProjectAccessServiceImpl implements ProjectAccessService {

    private static final String PROJECT_NOT_FOUND_MESSAGE = "Projeto nao encontrado";
    private static final String PROJECT_ACCESS_DENIED_MESSAGE = "Usuario nao possui permissao para acessar o projeto";

    private final ProjectRepositoryPort projectRepository;
    private final CurrentUserPort currentUserPort;

    public ProjectAccessServiceImpl(ProjectRepositoryPort projectRepository,
                                    CurrentUserPort currentUserPort) {
        this.projectRepository = projectRepository;
        this.currentUserPort = currentUserPort;
    }

    /**
     * Busca um projeto que esteja acessivel ao usuario autenticado.
     *
     * @param projectId identificador do projeto solicitado
     * @return projeto encontrado e autorizado para o usuario atual
     * @throws EntityNotFoundException quando o projeto nao existe ou nao esta acessivel
     * para o usuario autenticado
     */
    @Override
    public Project findAuthorizedProject(UUID projectId) {
        AuthenticatedUser currentUser = currentUserPort.getCurrentUser();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException(PROJECT_NOT_FOUND_MESSAGE));

        ensureUserCanAccessProject(currentUser, project);
        return project;
    }

    private void ensureUserCanAccessProject(AuthenticatedUser currentUser, Project project) {
        if (!currentUser.isAdmin() && !project.getUserId().equals(currentUser.id())) {
            throw new UnauthorizedUserException(PROJECT_ACCESS_DENIED_MESSAGE);
        }
    }
}
