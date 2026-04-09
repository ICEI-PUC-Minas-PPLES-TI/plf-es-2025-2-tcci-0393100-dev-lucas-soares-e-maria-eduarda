package br.pucminas.graphtest.application.usecases.gce;

import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.domain.gce.model.GceNode;
import br.pucminas.graphtest.application.port.input.gce.UpdateGceNodeUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.records.GceOutput;
import br.pucminas.graphtest.application.port.input.gce.records.UpdateGceNodeInput;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.service.gce.interfaces.GceMutationService;
import br.pucminas.graphtest.application.service.gce.interfaces.GceValidationResultService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

/**
 * Caso de uso responsavel por atualizar um no existente do GCE.
 */
public class UpdateGceNodeUseCaseImpl implements UpdateGceNodeUseCasePort {

    private final GceRepositoryPort gceRepository;
    private final ProjectAccessService projectAccessService;
    private final GceValidationResultService gceValidationResultService;
    private final GceMutationService gceMutationService;

    public UpdateGceNodeUseCaseImpl(GceRepositoryPort gceRepository,
                                    ProjectAccessService projectAccessService,
                                    GceValidationResultService gceValidationResultService,
                                    GceMutationService gceMutationService) {
        this.gceRepository = gceRepository;
        this.projectAccessService = projectAccessService;
        this.gceValidationResultService = gceValidationResultService;
        this.gceMutationService = gceMutationService;
    }

    @Override
    public GceOutput execute(UpdateGceNodeInput input) {
        Gce graph = gceMutationService.loadAuthorizedGraph(input.gceId(), gceRepository, projectAccessService);
        GceNode currentNode = graph.findNode(input.nodeCode())
                .orElseThrow(() -> new IllegalArgumentException("No inexistente: " + input.nodeCode()));

        GceNode updatedNode = new GceNode(
                currentNode.getId(),
                currentNode.getCode(),
                currentNode.isOperator()
                        ? currentNode.getCode()
                        : input.label() != null && !input.label().isBlank() ? input.label() : currentNode.getLabel(),
                currentNode.getType(),
                currentNode.isOperator() && input.operatorType() != null
                        ? input.operatorType()
                        : currentNode.getOperatorType()
        );

        graph.replaceNode(updatedNode);
        gceMutationService.refreshOperatorLabels(graph);
        gceMutationService.validateAndThrow(graph, gceValidationResultService);
        return GceOutput.from(gceRepository.save(graph));
    }
}
