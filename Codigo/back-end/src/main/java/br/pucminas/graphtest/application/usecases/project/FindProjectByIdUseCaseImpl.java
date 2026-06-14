package br.pucminas.graphtest.application.usecases.project;

import br.pucminas.graphtest.application.domain.project.model.Project;
import br.pucminas.graphtest.application.port.input.project.FindProjectByIdUseCasePort;
import br.pucminas.graphtest.application.port.input.project.records.FindProjectByIdInput;
import br.pucminas.graphtest.application.port.input.project.records.ProjectOutput;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

/**
 * Caso de uso responsavel por localizar um projeto pelo identificador,
 * respeitando as regras de acesso do usuario autenticado.
 */
public class FindProjectByIdUseCaseImpl implements FindProjectByIdUseCasePort {

    private final ProjectAccessService projectAccessService;

    /**
     * Cria o caso de uso com a dependencia necessaria para validar o acesso ao
     * projeto solicitado.
     *
     * @param projectAccessService servico responsavel por localizar projetos
     * acessiveis ao usuario autenticado
     */
    public FindProjectByIdUseCaseImpl(ProjectAccessService projectAccessService) {
        this.projectAccessService = projectAccessService;
    }

    /**
     * Busca o projeto identificado na entrada e retorna sua representacao de
     * saida, desde que o usuario autenticado tenha acesso ao recurso.
     *
     * @param input dados de entrada contendo o identificador do projeto
     * @return dados do projeto encontrado
     */
    @Override
    public ProjectOutput execute(FindProjectByIdInput input) {
        Project project = projectAccessService.findAuthorizedProject(input.id());
        return ProjectOutput.from(project);
    }
}
