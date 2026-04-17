package br.pucminas.graphtest.application.usecases.decisiontable;

import br.pucminas.graphtest.application.port.input.decisiontable.ListDecisionTablesByProjectUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableOutput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.ListDecisionTablesByProjectInput;
import br.pucminas.graphtest.application.port.output.repositories.DecisionTableRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

import java.util.List;

/**
 * Caso de uso responsavel por listar tabelas de decisao de um projeto autorizado.
 */
public class ListDecisionTablesByProjectUseCaseImpl implements ListDecisionTablesByProjectUseCasePort {

    private final DecisionTableRepositoryPort decisionTableRepository;
    private final ProjectAccessService projectAccessService;

    public ListDecisionTablesByProjectUseCaseImpl(DecisionTableRepositoryPort decisionTableRepository,
                                                  ProjectAccessService projectAccessService) {
        this.decisionTableRepository = decisionTableRepository;
        this.projectAccessService = projectAccessService;
    }

    @Override
    public List<DecisionTableOutput> execute(ListDecisionTablesByProjectInput input) {
        projectAccessService.findAuthorizedProject(input.projectId());

        return decisionTableRepository.findAllByProjectId(input.projectId()).stream()
                .map(DecisionTableOutput::from)
                .toList();
    }
}
