package br.pucminas.graphtest.application.service;

import br.pucminas.graphtest.application.domain.Project;
import br.pucminas.graphtest.application.domain.records.AuthenticatedUser;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepositoryPort;
import br.pucminas.graphtest.application.port.output.security.CurrentUserPort;
import br.pucminas.graphtest.application.service.interfaces.ProjectAccessService;
import java.util.Optional;
import java.util.UUID;

/**
 * Servico responsavel por localizar projetos visiveis para o usuario autenticado.
 * Administradores podem acessar qualquer projeto, enquanto os demais usuarios
 * somente podem acessar projetos associados ao proprio identificador.
 */
public class ProjectAccessServiceImpl implements ProjectAccessService {

    private static final String PROJECT_NOT_FOUND_MESSAGE = "Projeto nao encontrado";

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
        return findProjectFor(currentUser, projectId)
                .orElseThrow(() -> new EntityNotFoundException(PROJECT_NOT_FOUND_MESSAGE));
    }

    /**
     * Executa a busca do projeto conforme o perfil do usuario autenticado.
     *
     * @param currentUser usuario autenticado usado na verificacao de acesso
     * @param projectId identificador do projeto solicitado
     * @return {@link Optional} contendo o projeto quando encontrado e acessivel;
     * vazio caso contrario
     */
    private Optional<Project> findProjectFor(AuthenticatedUser currentUser, UUID projectId) {
        return currentUser.isAdmin() ? projectRepository.findById(projectId) : projectRepository.findByIdAndUserId(projectId, currentUser.id());
    }
}
