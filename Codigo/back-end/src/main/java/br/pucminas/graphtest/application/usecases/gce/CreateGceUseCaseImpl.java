package br.pucminas.graphtest.application.usecases.gce;

import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.port.input.gce.CreateGceUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.records.CreateGceInput;
import br.pucminas.graphtest.application.port.input.gce.records.GceOutput;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.service.gce.interfaces.GceMutationService;
import br.pucminas.graphtest.application.service.gce.interfaces.GceValidationResultService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

/**
 * Caso de uso responsavel por criar um novo GCE.
 */
public class CreateGceUseCaseImpl implements CreateGceUseCasePort {

    private final GceRepositoryPort gceRepository;
    private final GceValidationResultService gceValidationResultService;
    private final ProjectAccessService projectAccessService;
    private final GceMutationService gceMutationService;

    /**
     * Cria o caso de uso com as dependencias necessarias para validar e persistir um GCE.
     *
     * @param gceRepository repositorio responsavel pela persistencia do agregado
     * @param gceValidationResultService servico responsavel pela validacao do modelo
     */
    public CreateGceUseCaseImpl(
            GceRepositoryPort gceRepository,
            GceValidationResultService gceValidationResultService,
            ProjectAccessService projectAccessService,
            GceMutationService gceMutationService
    ) {
        this.gceRepository = gceRepository;
        this.gceValidationResultService = gceValidationResultService;
        this.projectAccessService = projectAccessService;
        this.gceMutationService = gceMutationService;
    }

    /**
     * Cria, valida e persiste um novo GCE.
     *
     * @param input dados de entrada do grafo
     * @return representacao do GCE persistido
     */
    @Override
    public GceOutput execute(CreateGceInput input) {
        projectAccessService.findAuthorizedProject(input.projectId());

        Gce graph = new Gce(
                null,
                input.projectId(),
                input.name(),
                input.description(),
                Boolean.TRUE.equals(input.selected()),
                gceMutationService.toNodes(input.nodes()),
                gceMutationService.toEdges(input.nodes(), input.edges()),
                gceMutationService.toRestrictions(input.restrictions())
        );
        graph.markCreatedNow();

        gceMutationService.refreshOperatorLabels(graph);
        gceMutationService.validateAndThrow(graph, gceValidationResultService);

        return GceOutput.from(gceRepository.save(graph));
    }
}
