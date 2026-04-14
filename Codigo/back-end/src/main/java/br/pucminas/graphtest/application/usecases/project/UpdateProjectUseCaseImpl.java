package br.pucminas.graphtest.application.usecases.project;

import br.pucminas.graphtest.application.domain.project.model.Project;
import br.pucminas.graphtest.application.port.input.project.UpdateProjectUseCasePort;
import br.pucminas.graphtest.application.port.input.project.records.ProjectOutput;
import br.pucminas.graphtest.application.port.input.project.records.UpdateProjectInput;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Caso de uso responsavel por atualizar os dados de um projeto acessivel ao
 * usuario autenticado.
 */
public class UpdateProjectUseCaseImpl implements UpdateProjectUseCasePort {

    private final ProjectRepositoryPort projectRepository;
    private final ProjectAccessService projectAccessService;

    /**
     * Cria o caso de uso com as dependencias necessarias para validar acesso e
     * persistir alteracoes em projetos.
     *
     * @param projectRepository repositorio usado para salvar as alteracoes
     * @param projectAccessService servico responsavel por localizar projetos
     * acessiveis ao usuario autenticado
     */
    public UpdateProjectUseCaseImpl(ProjectRepositoryPort projectRepository,
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
        boolean changed = false;

        if (isUpdatableValue(input.name(), project.getName())) {
            project.setName(input.name());
            changed = true;
        }

        if (isUpdatableValue(input.description(), project.getDescription())) {
            project.setDescription(input.description());
            changed = true;
        }

        if (!changed) return ProjectOutput.from(project);

        project.setUpdatedAt(LocalDateTime.now());
        return ProjectOutput.from(projectRepository.save(project));
    }


    private boolean isUpdatableValue(String newValue, String currentValue) {
        return newValue != null
                && !newValue.isBlank()
                && !Objects.equals(currentValue, newValue);
    }
}
