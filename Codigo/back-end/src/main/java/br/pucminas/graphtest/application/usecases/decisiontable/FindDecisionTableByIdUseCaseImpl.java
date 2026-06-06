package br.pucminas.graphtest.application.usecases.decisiontable;

import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.decisiontable.FindDecisionTableByIdUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableByIdInput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableOutput;
import br.pucminas.graphtest.application.port.output.repositories.DecisionTableRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

/**
 * Caso de uso responsavel por localizar uma tabela de decisao pelo identificador proprio.
 */
public class FindDecisionTableByIdUseCaseImpl implements FindDecisionTableByIdUseCasePort {

    private final DecisionTableRepositoryPort decisionTableRepository;
    private final ProjectAccessService projectAccessService;

    public FindDecisionTableByIdUseCaseImpl(DecisionTableRepositoryPort decisionTableRepository,
                                            ProjectAccessService projectAccessService) {
        this.decisionTableRepository = decisionTableRepository;
        this.projectAccessService = projectAccessService;
    }

    @Override
    public DecisionTableOutput execute(DecisionTableByIdInput input) {
        var decisionTable = decisionTableRepository.findById(input.id())
                .orElseThrow(() -> new EntityNotFoundException("Tabela de decisao nao encontrada"));

        projectAccessService.findAuthorizedProject(decisionTable.getProjectId());
        if (!decisionTable.getProjectId().equals(input.projectId())) {
            throw new EntityNotFoundException("Tabela de decisao nao encontrada");
        }
        return DecisionTableOutput.from(decisionTable);
    }
}
