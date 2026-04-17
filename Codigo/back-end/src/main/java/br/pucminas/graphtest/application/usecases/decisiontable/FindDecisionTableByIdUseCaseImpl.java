package br.pucminas.graphtest.application.usecases.decisiontable;

import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTable;
import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.decisiontable.FindDecisionTableByIdUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableByIdInput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableOutput;
import br.pucminas.graphtest.application.port.output.repositories.DecisionTableRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.service.decisiontable.interfaces.DecisionTableSyncService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

/**
 * Caso de uso responsavel por localizar uma tabela de decisao pelo identificador proprio.
 */
public class FindDecisionTableByIdUseCaseImpl implements FindDecisionTableByIdUseCasePort {

    private final DecisionTableRepositoryPort decisionTableRepository;
    private final GceRepositoryPort gceRepository;
    private final ProjectAccessService projectAccessService;
    private final DecisionTableSyncService decisionTableSyncService;

    public FindDecisionTableByIdUseCaseImpl(DecisionTableRepositoryPort decisionTableRepository,
                                            GceRepositoryPort gceRepository,
                                            ProjectAccessService projectAccessService,
                                            DecisionTableSyncService decisionTableSyncService) {
        this.decisionTableRepository = decisionTableRepository;
        this.gceRepository = gceRepository;
        this.projectAccessService = projectAccessService;
        this.decisionTableSyncService = decisionTableSyncService;
    }

    @Override
    public DecisionTableOutput execute(DecisionTableByIdInput input) {
        DecisionTable decisionTable = decisionTableRepository.findById(input.id())
                .orElseThrow(() -> new EntityNotFoundException("Tabela de decisao nao encontrada"));

        projectAccessService.findAuthorizedProject(decisionTable.getProjectId());

        DecisionTable syncedTable = synchronizeIfNecessary(decisionTable);
        return DecisionTableOutput.from(syncedTable);
    }

    private DecisionTable synchronizeIfNecessary(DecisionTable decisionTable) {
        Gce graph = loadCurrentGraph(decisionTable);
        if (graph == null || decisionTableSyncService.isStale(decisionTable, graph)) {
            decisionTable.markAsStale();
            return decisionTableRepository.save(decisionTable);
        }
        return decisionTable;
    }

    private Gce loadCurrentGraph(DecisionTable decisionTable) {
        return decisionTable.getGceId() == null
                ? null
                : gceRepository.findById(decisionTable.getGceId()).orElse(null);
    }
}
