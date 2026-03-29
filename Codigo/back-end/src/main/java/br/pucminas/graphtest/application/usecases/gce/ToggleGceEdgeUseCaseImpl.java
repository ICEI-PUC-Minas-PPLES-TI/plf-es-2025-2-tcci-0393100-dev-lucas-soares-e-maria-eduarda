package br.pucminas.graphtest.application.usecases.gce;

import br.pucminas.graphtest.application.domain.Gce;
import br.pucminas.graphtest.application.domain.GceEdge;
import br.pucminas.graphtest.application.domain.enums.GceEdgeTypeEnum;
import br.pucminas.graphtest.application.port.input.gce.ToggleGceEdgeUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.records.GceOutput;
import br.pucminas.graphtest.application.port.input.gce.records.ToggleGceEdgeInput;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.service.interfaces.GceMutationService;
import br.pucminas.graphtest.application.service.interfaces.GceValidationResultService;
import br.pucminas.graphtest.application.service.interfaces.ProjectAccessService;

/**
 * Caso de uso responsavel por inverter o tipo de uma aresta existente do GCE.
 */
public class ToggleGceEdgeUseCaseImpl implements ToggleGceEdgeUseCasePort {

    private final GceRepositoryPort gceRepository;
    private final ProjectAccessService projectAccessService;
    private final GceValidationResultService gceValidationResultService;
    private final GceMutationService gceMutationService;

    public ToggleGceEdgeUseCaseImpl(GceRepositoryPort gceRepository,
                                    ProjectAccessService projectAccessService,
                                    GceValidationResultService gceValidationResultService,
                                    GceMutationService gceMutationService) {
        this.gceRepository = gceRepository;
        this.projectAccessService = projectAccessService;
        this.gceValidationResultService = gceValidationResultService;
        this.gceMutationService = gceMutationService;
    }

    @Override
    public GceOutput execute(ToggleGceEdgeInput input) {
        Gce graph = gceMutationService.loadAuthorizedGraph(input.gceId(), gceRepository, projectAccessService);
        GceEdge currentEdge = graph.findEdge(input.edgeId())
                .orElseThrow(() -> new IllegalArgumentException("Aresta inexistente: " + input.edgeId()));

        GceEdgeTypeEnum toggledType = currentEdge.getType() == GceEdgeTypeEnum.IDENTITY
                ? GceEdgeTypeEnum.NEGATED
                : GceEdgeTypeEnum.IDENTITY;

        graph.replaceEdge(new GceEdge(
                currentEdge.getId(),
                currentEdge.getSourceNodeCode(),
                currentEdge.getTargetNodeCode(),
                toggledType
        ));

        gceMutationService.validateAndThrow(graph, gceValidationResultService);
        return GceOutput.from(gceRepository.save(graph));
    }
}
