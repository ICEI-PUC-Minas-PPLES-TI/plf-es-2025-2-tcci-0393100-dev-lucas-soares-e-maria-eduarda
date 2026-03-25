package br.pucminas.graphtest.application.usecases.project;

import br.pucminas.graphtest.application.domain.Project;
import br.pucminas.graphtest.application.port.input.project.DeleteProjectUseCase;
import br.pucminas.graphtest.application.port.input.project.records.DeleteProjectInput;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepository;
import br.pucminas.graphtest.application.service.interfaces.ProjectAccessService;

/**
 * Caso de uso responsavel por excluir um projeto acessivel ao usuario
 * autenticado. A remocao somente e realizada apos a validacao de acesso ao
 * projeto solicitado.
 */
public class DeleteProjectUseCaseImpl implements DeleteProjectUseCase {

    private final ProjectRepository projectRepository;
    private final ProjectAccessService projectAccessService;

    /**
     * Cria o caso de uso com as dependencias necessarias para validar acesso e
     * remover projetos persistidos.
     *
     * @param projectRepository repositorio usado para excluir projetos
     * @param projectAccessService servico responsavel por validar o acesso ao
     * projeto informado
     */
    public DeleteProjectUseCaseImpl(ProjectRepository projectRepository,
                                    ProjectAccessService projectAccessService) {
        this.projectRepository = projectRepository;
        this.projectAccessService = projectAccessService;
    }

    /**
     * Exclui o projeto identificado na entrada, desde que esteja acessivel ao
     * usuario autenticado.
     *
     * @param input dados de entrada contendo o identificador do projeto
     */
    @Override
    public void execute(DeleteProjectInput input) {
        Project project = projectAccessService.findAuthorizedProject(input.id());
        projectRepository.deleteById(project.getId());
    }
}
