package br.pucminas.graphtest.application.usecases.decisiontable;

import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTable;
import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.port.input.decisiontable.ListDecisionTablesByProjectUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableOutput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.ListDecisionTablesByProjectInput;
import br.pucminas.graphtest.application.port.output.repositories.DecisionTableRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.service.decisiontable.interfaces.DecisionTableSyncService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

import java.util.List;

/**
 * Caso de uso responsavel por listar tabelas de decisao de um projeto autorizado.
 */
public class ListDecisionTablesByProjectUseCaseImpl implements ListDecisionTablesByProjectUseCasePort {

    private final DecisionTableRepositoryPort decisionTableRepository;
    private final GceRepositoryPort gceRepository;
    private final ProjectAccessService projectAccessService;
    private final DecisionTableSyncService decisionTableSyncService;

    public ListDecisionTablesByProjectUseCaseImpl(DecisionTableRepositoryPort decisionTableRepository,
                                                  GceRepositoryPort gceRepository,
                                                  ProjectAccessService projectAccessService,
                                                  DecisionTableSyncService decisionTableSyncService) {
        this.decisionTableRepository = decisionTableRepository;
        this.gceRepository = gceRepository;
        this.projectAccessService = projectAccessService;
        this.decisionTableSyncService = decisionTableSyncService;
    }

    @Override
    public List<DecisionTableOutput> execute(ListDecisionTablesByProjectInput input) {
        projectAccessService.findAuthorizedProject(input.projectId());

        return decisionTableRepository.findAllByProjectId(input.projectId()).stream()
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
