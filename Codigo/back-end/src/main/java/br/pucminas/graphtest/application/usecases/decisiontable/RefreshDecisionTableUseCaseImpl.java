package br.pucminas.graphtest.application.usecases.decisiontable;

import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTable;
import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.decisiontable.RefreshDecisionTableUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableByGceIdInput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableOutput;
import br.pucminas.graphtest.application.port.output.repositories.DecisionTableRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.service.decisiontable.interfaces.DecisionTableDerivationService;
import br.pucminas.graphtest.application.service.gce.interfaces.GceMutationService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

/**
 * Caso de uso responsavel por regenerar a tabela de decisao de um GCE existente.
 */
public class RefreshDecisionTableUseCaseImpl implements RefreshDecisionTableUseCasePort {

    private final DecisionTableRepositoryPort decisionTableRepository;
    private final GceRepositoryPort gceRepository;
    private final ProjectAccessService projectAccessService;
    private final GceMutationService gceMutationService;
    private final DecisionTableDerivationService decisionTableDerivationService;

    public RefreshDecisionTableUseCaseImpl(DecisionTableRepositoryPort decisionTableRepository,
                                           GceRepositoryPort gceRepository,
                                           ProjectAccessService projectAccessService,
                                           GceMutationService gceMutationService,
                                           DecisionTableDerivationService decisionTableDerivationService) {
        this.decisionTableRepository = decisionTableRepository;
        this.gceRepository = gceRepository;
        this.projectAccessService = projectAccessService;
        this.gceMutationService = gceMutationService;
        this.decisionTableDerivationService = decisionTableDerivationService;
    }

    @Override
    public DecisionTableOutput execute(DecisionTableByGceIdInput input) {
        DecisionTable currentTable = decisionTableRepository.findByGceId(input.gceId())
                .orElseThrow(() -> new EntityNotFoundException("Tabela de decisao nao encontrada"));

        Gce graph = gceMutationService.loadAuthorizedGraph(input.gceId(), gceRepository, projectAccessService);
        DecisionTable refreshedTable = decisionTableDerivationService.derive(graph, currentTable);
        return DecisionTableOutput.from(decisionTableRepository.save(refreshedTable));
    }
}
