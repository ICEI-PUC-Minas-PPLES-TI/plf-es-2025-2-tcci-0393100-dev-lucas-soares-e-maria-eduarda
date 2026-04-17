package br.pucminas.graphtest.application.usecases.decisiontable;

import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTable;
import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.decisiontable.FindDecisionTableByGceIdUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableByGceIdInput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableOutput;
import br.pucminas.graphtest.application.port.output.repositories.DecisionTableRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.service.decisiontable.interfaces.DecisionTableSyncService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

/**
 * Caso de uso responsavel por localizar a tabela de decisao atualmente associada a um GCE.
 */
public class FindDecisionTableByGceIdUseCaseImpl implements FindDecisionTableByGceIdUseCasePort {

    private final DecisionTableRepositoryPort decisionTableRepository;
    private final GceRepositoryPort gceRepository;
    private final ProjectAccessService projectAccessService;
    private final DecisionTableSyncService decisionTableSyncService;

    public FindDecisionTableByGceIdUseCaseImpl(DecisionTableRepositoryPort decisionTableRepository,
                                               GceRepositoryPort gceRepository,
                                               ProjectAccessService projectAccessService,
                                               DecisionTableSyncService decisionTableSyncService) {
        this.decisionTableRepository = decisionTableRepository;
        this.gceRepository = gceRepository;
        this.projectAccessService = projectAccessService;
        this.decisionTableSyncService = decisionTableSyncService;
    }

    @Override
    public DecisionTableOutput execute(DecisionTableByGceIdInput input) {
        DecisionTable decisionTable = decisionTableRepository.findByGceId(input.gceId())
                .orElseThrow(() -> new EntityNotFoundException("Tabela de decisao nao encontrada"));

        projectAccessService.findAuthorizedProject(decisionTable.getProjectId());

        Gce graph = gceRepository.findById(decisionTable.getGceId()).orElse(null);
        if (graph == null || decisionTableSyncService.isStale(decisionTable, graph)) {
            decisionTable.markAsStale();
            decisionTable = decisionTableRepository.save(decisionTable);
        }

        return DecisionTableOutput.from(decisionTable);
    }
}
