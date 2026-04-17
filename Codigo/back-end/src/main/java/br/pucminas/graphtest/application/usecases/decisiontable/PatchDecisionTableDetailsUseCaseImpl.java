package br.pucminas.graphtest.application.usecases.decisiontable;

import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTable;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.decisiontable.PatchDecisionTableDetailsUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableOutput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.UpdateDecisionTableDetailsInput;
import br.pucminas.graphtest.application.port.output.repositories.DecisionTableRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

import java.time.LocalDateTime;

public class PatchDecisionTableDetailsUseCaseImpl implements PatchDecisionTableDetailsUseCasePort {

    private final DecisionTableRepositoryPort decisionTableRepository;
    private final ProjectAccessService projectAccessService;

    public PatchDecisionTableDetailsUseCaseImpl(DecisionTableRepositoryPort decisionTableRepository,
                                                ProjectAccessService projectAccessService) {
        this.decisionTableRepository = decisionTableRepository;
        this.projectAccessService = projectAccessService;
    }

    @Override
    public DecisionTableOutput execute(UpdateDecisionTableDetailsInput input) {
        DecisionTable decisionTable = decisionTableRepository.findById(input.id())
                .orElseThrow(() -> new EntityNotFoundException("Tabela de decisao nao encontrada"));

        projectAccessService.findAuthorizedProject(decisionTable.getProjectId());
        decisionTable.updateDetails(input.name(), input.description());
        decisionTable.setUpdatedAt(LocalDateTime.now());
        return DecisionTableOutput.from(decisionTableRepository.save(decisionTable));
    }
}
