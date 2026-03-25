package br.pucminas.graphtest.application.usecases.project;

import br.pucminas.graphtest.application.domain.Project;
import br.pucminas.graphtest.application.port.input.project.UpdateProjectUseCase;
import br.pucminas.graphtest.application.port.input.project.records.ProjectOutput;
import br.pucminas.graphtest.application.port.input.project.records.UpdateProjectInput;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepository;
import br.pucminas.graphtest.application.service.interfaces.ProjectAccessService;

/**
 * Caso de uso responsavel por atualizar os dados de um projeto acessivel ao
 * usuario autenticado.
 */
public class UpdateProjectUseCaseImpl implements UpdateProjectUseCase {

    private final ProjectRepository projectRepository;
    private final ProjectAccessService projectAccessService;

    /**
     * Cria o caso de uso com as dependencias necessarias para validar acesso e
     * persistir alteracoes em projetos.
     *
     * @param projectRepository repositorio usado para salvar as alteracoes
     * @param projectAccessService servico responsavel por localizar projetos
     * acessiveis ao usuario autenticado
     */
    public UpdateProjectUseCaseImpl(ProjectRepository projectRepository,
                                    ProjectAccessService projectAccessService) {
        this.projectRepository = projectRepository;
        this.projectAccessService = projectAccessService;
    }

    /**
     * Atualiza o nome e a descricao do projeto identificado na entrada, desde
     * que o usuario autenticado tenha acesso ao recurso.
     *
     * @param input dados de entrada com o identificador e os novos valores do
     * projeto
     * @return representacao do projeto atualizado e persistido
     */
    @Override
    public ProjectOutput execute(UpdateProjectInput input) {
        Project project = projectAccessService.findAuthorizedProject(input.id());

        updateNameIfProvided(project, input.name());
        updateDescriptionIfProvided(project, input.description());

        return ProjectOutput.from(projectRepository.save(project));
    }

    private void updateNameIfProvided(Project project, String name) {
        if (name != null && !name.isBlank()) {
            project.setName(name);
        }
    }

    private void updateDescriptionIfProvided(Project project, String description) {
        if (description != null && !description.isBlank()) {
            project.setDescription(description);
        }
    }
}
