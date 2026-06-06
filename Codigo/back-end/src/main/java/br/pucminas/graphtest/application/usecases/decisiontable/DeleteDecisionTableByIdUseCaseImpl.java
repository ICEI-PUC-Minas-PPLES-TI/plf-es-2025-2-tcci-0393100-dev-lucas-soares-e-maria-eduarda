package br.pucminas.graphtest.application.usecases.decisiontable;

import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTable;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.decisiontable.DeleteDecisionTableByIdUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableByIdInput;
import br.pucminas.graphtest.application.port.output.repositories.DecisionTableRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

/**
 * Caso de uso responsavel por excluir a tabela de decisao pelo identificador proprio.
 */
public class DeleteDecisionTableByIdUseCaseImpl implements DeleteDecisionTableByIdUseCasePort {

    private final DecisionTableRepositoryPort decisionTableRepository;
    private final ProjectAccessService projectAccessService;

    public DeleteDecisionTableByIdUseCaseImpl(DecisionTableRepositoryPort decisionTableRepository,
                                              ProjectAccessService projectAccessService) {
        this.decisionTableRepository = decisionTableRepository;
        this.projectAccessService = projectAccessService;
    }

    @Override
    public void execute(DecisionTableByIdInput input) {
        DecisionTable decisionTable = decisionTableRepository.findById(input.id())
                .orElseThrow(() -> new EntityNotFoundException("Tabela de decisao nao encontrada"));

        projectAccessService.findAuthorizedProject(decisionTable.getProjectId());
        if (!decisionTable.getProjectId().equals(input.projectId())) {
            throw new EntityNotFoundException("Tabela de decisao nao encontrada");
        }
        decisionTableRepository.deleteById(decisionTable.getId());
    }
}
