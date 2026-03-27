package br.pucminas.graphtest.application.usecases.project;

import br.pucminas.graphtest.application.domain.Project;
import br.pucminas.graphtest.application.domain.records.AuthenticatedUser;
import br.pucminas.graphtest.application.port.input.project.CreateProjectUseCasePort;
import br.pucminas.graphtest.application.port.input.project.records.CreateProjectInput;
import br.pucminas.graphtest.application.port.input.project.records.ProjectOutput;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepositoryPort;
import br.pucminas.graphtest.application.port.output.security.CurrentUserPort;

/**
 * Caso de uso responsavel por criar um novo projeto para o usuario autenticado.
 */
public class CreateProjectUseCaseImpl implements CreateProjectUseCasePort {

    private final ProjectRepositoryPort projectRepository;
    private final CurrentUserPort currentUserPort;

    /**
     * Cria o caso de uso com as dependencias necessarias para persistir projetos
     * e identificar o usuario autenticado.
     *
     * @param projectRepository repositorio usado para salvar e contar projetos
     * @param currentUserPort porta responsavel por fornecer o usuario autenticado
     */
    public CreateProjectUseCaseImpl(ProjectRepositoryPort projectRepository, CurrentUserPort currentUserPort) {
        this.projectRepository = projectRepository;
        this.currentUserPort = currentUserPort;
    }

    /**
     * Cria um projeto vinculado ao usuario autenticado com os dados recebidos.
     *
     * @param input dados de entrada para criacao do projeto
     * @return representacao do projeto criado e persistido
     */
    @Override
    public ProjectOutput execute(CreateProjectInput input) {
        AuthenticatedUser currentUser = currentUserPort.getCurrentUser();
        String projectName = resolveProjectName(input.name(), currentUser.id());

        Project project = new Project(
                null,
                projectName,
                input.description(),
                currentUser.id()
        );

        return ProjectOutput.from(projectRepository.save(project));
    }

    private String resolveProjectName(String requestedName, java.util.UUID userId) {
        if (requestedName != null && !requestedName.isBlank()) {
            return requestedName;
        }

        long existingProjects = projectRepository.countByUserId(userId);
        return "Projeto " + (existingProjects + 1);
    }
}
