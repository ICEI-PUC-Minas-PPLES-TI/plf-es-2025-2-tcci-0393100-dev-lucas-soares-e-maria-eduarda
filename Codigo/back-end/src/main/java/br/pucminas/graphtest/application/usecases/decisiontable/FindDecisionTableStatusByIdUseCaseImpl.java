package br.pucminas.graphtest.application.usecases.decisiontable;

import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTable;
import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.decisiontable.FindDecisionTableStatusByIdUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableByIdInput;
import br.pucminas.graphtest.application.port.output.repositories.DecisionTableRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.service.decisiontable.interfaces.DecisionTableSyncService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

/**
 * Caso de uso responsavel por verificar se a tabela de decisao segue sincronizada com o GCE associado.
 */
public class FindDecisionTableStatusByIdUseCaseImpl implements FindDecisionTableStatusByIdUseCasePort {

    private final DecisionTableRepositoryPort decisionTableRepository;
    private final GceRepositoryPort gceRepository;
    private final ProjectAccessService projectAccessService;
    private final DecisionTableSyncService decisionTableSyncService;

    public FindDecisionTableStatusByIdUseCaseImpl(DecisionTableRepositoryPort decisionTableRepository,
                                                  GceRepositoryPort gceRepository,
                                                  ProjectAccessService projectAccessService,
                                                  DecisionTableSyncService decisionTableSyncService) {
        this.decisionTableRepository = decisionTableRepository;
        this.gceRepository = gceRepository;
        this.projectAccessService = projectAccessService;
        this.decisionTableSyncService = decisionTableSyncService;
    }

    @Override
    public boolean execute(DecisionTableByIdInput input) {
        DecisionTable decisionTable = decisionTableRepository.findById(input.id())
                .orElseThrow(() -> new EntityNotFoundException("Tabela de decisao nao encontrada"));

        projectAccessService.findAuthorizedProject(decisionTable.getProjectId());
        Gce graph = loadCurrentGraph(decisionTable);

        return graph != null && !decisionTableSyncService.isStale(decisionTable, graph);
    }

    private Gce loadCurrentGraph(DecisionTable decisionTable) {
        if (decisionTable.getGceId() == null) {
            return null;
        }

        return gceRepository.findById(decisionTable.getGceId()).orElse(null);
    }
}
