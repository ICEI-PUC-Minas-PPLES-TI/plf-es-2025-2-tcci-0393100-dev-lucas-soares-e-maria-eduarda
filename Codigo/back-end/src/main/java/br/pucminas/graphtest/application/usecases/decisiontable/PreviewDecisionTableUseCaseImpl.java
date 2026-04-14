package br.pucminas.graphtest.application.usecases.decisiontable;

import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTable;
import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.port.input.decisiontable.PreviewDecisionTableUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableByGceIdInput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableOutput;
import br.pucminas.graphtest.application.port.output.repositories.DecisionTableRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.service.decisiontable.interfaces.DecisionTableDerivationService;
import br.pucminas.graphtest.application.service.gce.interfaces.GceMutationService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

/**
 * Caso de uso responsavel por derivar em memoria a tabela de decisao de um GCE, sem persistencia.
 */
public class PreviewDecisionTableUseCaseImpl implements PreviewDecisionTableUseCasePort {

    private final DecisionTableRepositoryPort decisionTableRepository;
    private final GceRepositoryPort gceRepository;
    private final ProjectAccessService projectAccessService;
    private final GceMutationService gceMutationService;
    private final DecisionTableDerivationService decisionTableDerivationService;

    public PreviewDecisionTableUseCaseImpl(DecisionTableRepositoryPort decisionTableRepository,
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
        Gce graph = gceMutationService.loadAuthorizedGraph(input.gceId(), gceRepository, projectAccessService);
        DecisionTable currentTable = decisionTableRepository.findByGceId(input.gceId()).orElse(null);
        DecisionTable previewTable = decisionTableDerivationService.derive(graph, currentTable);
        return DecisionTableOutput.from(previewTable);
    }
}
