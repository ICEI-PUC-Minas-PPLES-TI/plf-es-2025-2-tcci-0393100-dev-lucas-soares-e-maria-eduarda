package br.pucminas.graphtest.application.usecases.project;

import br.pucminas.graphtest.application.domain.project.model.Project;
import br.pucminas.graphtest.application.port.input.project.DeleteProjectUseCasePort;
import br.pucminas.graphtest.application.port.input.project.records.DeleteProjectInput;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectDeletionService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso responsavel por excluir um projeto acessivel ao usuario
 * autenticado. A remocao somente e realizada apos a validacao de acesso ao
 * projeto solicitado.
 */
public class DeleteProjectUseCaseImpl implements DeleteProjectUseCasePort {

    private final ProjectAccessService projectAccessService;
    private final ProjectDeletionService projectDeletionService;

    /**
     * Cria o caso de uso com as dependencias necessarias para validar acesso e
     * remover projetos persistidos.
     *
     * @param projectDeletionService serviço usado para excluir projetos
     * @param projectAccessService servico responsavel por validar o acesso ao
     * projeto informado
     */
    public DeleteProjectUseCaseImpl(ProjectAccessService projectAccessService,
                                    ProjectDeletionService projectDeletionService) {
        this.projectAccessService = projectAccessService;
        this.projectDeletionService = projectDeletionService;
    }

    /**
     * Exclui o projeto identificado na entrada, desde que esteja acessivel ao
     * usuario autenticado.
     *
     * @param input dados de entrada contendo o identificador do projeto
     */
    @Override
    @Transactional
    public void execute(DeleteProjectInput input) {
        Project project = projectAccessService.findAuthorizedProject(input.id());
        projectDeletionService.deleteProject(project);
    }
}
