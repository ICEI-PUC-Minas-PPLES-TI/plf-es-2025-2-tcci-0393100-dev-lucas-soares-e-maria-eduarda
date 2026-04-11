package br.pucminas.graphtest.application.usecases.gce;

import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.domain.gce.model.GceEdge;
import br.pucminas.graphtest.application.domain.gce.enums.GceEdgeTypeEnum;
import br.pucminas.graphtest.application.port.input.gce.ToggleGceEdgeUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.records.GceOutput;
import br.pucminas.graphtest.application.port.input.gce.records.ToggleGceEdgeInput;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.service.gce.interfaces.GceMutationService;
import br.pucminas.graphtest.application.service.gce.interfaces.GceValidationResultService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

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

        GceEdge updatedEdge = new GceEdge(
                currentEdge.getId(),
                currentEdge.getSourceNodeCode(),
                currentEdge.getTargetNodeCode(),
                toggledType
        );
        updatedEdge.restoreAuditFields(currentEdge.getCreatedAt(), currentEdge.getUpdatedAt());
        updatedEdge.markUpdatedNow();

        graph.replaceEdge(updatedEdge);

        gceMutationService.validateAndThrow(graph, gceValidationResultService);
        graph.markUpdatedNow();
        return GceOutput.from(gceRepository.save(graph));
    }
}
