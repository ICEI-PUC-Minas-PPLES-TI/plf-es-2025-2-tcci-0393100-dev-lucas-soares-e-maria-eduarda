package br.pucminas.graphtest.application.usecases.decisiontable;

import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTable;
import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.port.input.decisiontable.ListDecisionTablesUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableOutput;
import br.pucminas.graphtest.application.port.output.repositories.DecisionTableRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepositoryPort;
import br.pucminas.graphtest.application.port.output.security.CurrentUserPort;
import br.pucminas.graphtest.application.security.AuthenticatedUser;
import br.pucminas.graphtest.application.service.decisiontable.interfaces.DecisionTableSyncService;

import java.util.List;

public class ListDecisionTablesUseCaseImpl implements ListDecisionTablesUseCasePort {

    private final DecisionTableRepositoryPort decisionTableRepository;
    private final GceRepositoryPort gceRepository;
    private final ProjectRepositoryPort projectRepository;
    private final CurrentUserPort currentUserPort;
    private final DecisionTableSyncService decisionTableSyncService;

    public ListDecisionTablesUseCaseImpl(DecisionTableRepositoryPort decisionTableRepository,
                                         GceRepositoryPort gceRepository,
                                         ProjectRepositoryPort projectRepository,
                                         CurrentUserPort currentUserPort,
                                         DecisionTableSyncService decisionTableSyncService) {
        this.decisionTableRepository = decisionTableRepository;
        this.gceRepository = gceRepository;
        this.projectRepository = projectRepository;
        this.currentUserPort = currentUserPort;
        this.decisionTableSyncService = decisionTableSyncService;
    }

    @Override
    public List<DecisionTableOutput> execute() {
        AuthenticatedUser currentUser = currentUserPort.getCurrentUser();

        return projectRepository.findAllByUserId(currentUser.id()).stream()
                .flatMap(project -> decisionTableRepository.findAllByProjectId(project.getId()).stream())
                .map(this::synchronizeIfNecessary)
                .map(DecisionTableOutput::from)
                .toList();
    }

    private DecisionTable synchronizeIfNecessary(DecisionTable decisionTable) {
        Gce graph = decisionTable.getGceId() == null
                ? null
                : gceRepository.findById(decisionTable.getGceId()).orElse(null);

        if (graph == null || decisionTableSyncService.isStale(decisionTable, graph)) {
            decisionTable.markAsStale();
            return decisionTableRepository.save(decisionTable);
        }

        return decisionTable;
    }
}
