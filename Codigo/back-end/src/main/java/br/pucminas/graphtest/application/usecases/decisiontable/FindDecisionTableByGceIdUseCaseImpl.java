package br.pucminas.graphtest.application.usecases.decisiontable;

import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.decisiontable.FindDecisionTableByGceIdUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableByGceIdInput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableOutput;
import br.pucminas.graphtest.application.port.output.repositories.DecisionTableRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

/**
 * Caso de uso responsavel por localizar a tabela de decisao atualmente associada a um GCE.
 */
public class FindDecisionTableByGceIdUseCaseImpl implements FindDecisionTableByGceIdUseCasePort {

    private final DecisionTableRepositoryPort decisionTableRepository;
    private final ProjectAccessService projectAccessService;

    public FindDecisionTableByGceIdUseCaseImpl(DecisionTableRepositoryPort decisionTableRepository,
                                               ProjectAccessService projectAccessService) {
        this.decisionTableRepository = decisionTableRepository;
        this.projectAccessService = projectAccessService;
    }

    @Override
    public DecisionTableOutput execute(DecisionTableByGceIdInput input) {
        var decisionTable = decisionTableRepository.findByGceId(input.gceId())
                .orElseThrow(() -> new EntityNotFoundException("Tabela de decisao nao encontrada"));

        projectAccessService.findAuthorizedProject(decisionTable.getProjectId());
        return DecisionTableOutput.from(decisionTable);
    }
}
