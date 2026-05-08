package br.pucminas.graphtest.application.usecases.decisiontable;

import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.exception.DecisionTableAlreadyExistsException;
import br.pucminas.graphtest.application.port.input.decisiontable.GenerateDecisionTableUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableOutput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.GenerateDecisionTableInput;
import br.pucminas.graphtest.application.port.output.repositories.DecisionTableRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.service.decisiontable.interfaces.DecisionTableDerivationService;
import br.pucminas.graphtest.application.service.gce.interfaces.GceMutationService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

/**
 * Caso de uso responsavel por gerar e persistir uma tabela de decisao a partir de um GCE existente.
 */
public class GenerateDecisionTableUseCaseImpl implements GenerateDecisionTableUseCasePort {

    private final DecisionTableRepositoryPort decisionTableRepository;
    private final GceRepositoryPort gceRepository;
    private final ProjectAccessService projectAccessService;
    private final GceMutationService gceMutationService;
    private final DecisionTableDerivationService decisionTableDerivationService;

    public GenerateDecisionTableUseCaseImpl(DecisionTableRepositoryPort decisionTableRepository,
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
    public DecisionTableOutput execute(GenerateDecisionTableInput input) {
        if (decisionTableRepository.existsByGceId(input.gceId())) {
            throw new DecisionTableAlreadyExistsException("Ja existe tabela de decisao gerada para o GCE informado.");
        }

        Gce graph = gceMutationService.loadAuthorizedGraph(input.gceId(), gceRepository, projectAccessService);
        var derivedTable = decisionTableDerivationService.derive(graph, null);
        return DecisionTableOutput.from(decisionTableRepository.save(derivedTable));
    }
}
