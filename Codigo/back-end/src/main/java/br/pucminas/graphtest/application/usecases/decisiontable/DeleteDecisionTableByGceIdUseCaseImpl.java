package br.pucminas.graphtest.application.usecases.decisiontable;

import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTable;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.decisiontable.DeleteDecisionTableByGceIdUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableByGceIdInput;
import br.pucminas.graphtest.application.port.output.repositories.DecisionTableRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

/**
 * Caso de uso responsavel por excluir manualmente a tabela de decisao vinculada a um GCE atual.
 */
public class DeleteDecisionTableByGceIdUseCaseImpl implements DeleteDecisionTableByGceIdUseCasePort {

    private final DecisionTableRepositoryPort decisionTableRepository;
    private final ProjectAccessService projectAccessService;

    public DeleteDecisionTableByGceIdUseCaseImpl(DecisionTableRepositoryPort decisionTableRepository,
                                                 ProjectAccessService projectAccessService) {
        this.decisionTableRepository = decisionTableRepository;
        this.projectAccessService = projectAccessService;
    }

    @Override
    public void execute(DecisionTableByGceIdInput input) {
        DecisionTable decisionTable = decisionTableRepository.findByGceId(input.gceId())
                .orElseThrow(() -> new EntityNotFoundException("Tabela de decisao nao encontrada"));

        projectAccessService.findAuthorizedProject(decisionTable.getProjectId());
        if (!decisionTable.getProjectId().equals(input.projectId())) {
            throw new EntityNotFoundException("Tabela de decisao nao encontrada");
        }
        decisionTableRepository.deleteById(decisionTable.getId());
    }
}
