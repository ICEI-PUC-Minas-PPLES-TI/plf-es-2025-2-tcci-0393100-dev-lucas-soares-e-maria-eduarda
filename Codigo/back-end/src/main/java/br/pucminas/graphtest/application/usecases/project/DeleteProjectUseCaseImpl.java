package br.pucminas.graphtest.application.usecases.project;

import br.pucminas.graphtest.application.domain.project.model.Project;
import br.pucminas.graphtest.application.port.input.project.DeleteProjectUseCasePort;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.port.input.project.records.DeleteProjectInput;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

/**
 * Caso de uso responsavel por excluir um projeto acessivel ao usuario
 * autenticado. A remocao somente e realizada apos a validacao de acesso ao
 * projeto solicitado.
 */
public class DeleteProjectUseCaseImpl implements DeleteProjectUseCasePort {

    private final ProjectRepositoryPort projectRepository;
    private final GceRepositoryPort gceRepository;
    private final ProjectAccessService projectAccessService;

    /**
     * Cria o caso de uso com as dependencias necessarias para validar acesso e
     * remover projetos persistidos.
     *
     * @param projectRepository repositorio usado para excluir projetos
     * @param projectAccessService servico responsavel por validar o acesso ao
     * projeto informado
     */
    public DeleteProjectUseCaseImpl(ProjectRepositoryPort projectRepository,
                                    GceRepositoryPort gceRepository,
                                    ProjectAccessService projectAccessService) {
        this.projectRepository = projectRepository;
        this.gceRepository = gceRepository;
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
        gceRepository.deleteAllByProjectId(project.getId());
        projectRepository.deleteById(project.getId());
    }
}
