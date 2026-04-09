package br.pucminas.graphtest.application.usecases.gce;

import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.port.input.gce.ValidateGceUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.records.ValidateGceInput;
import br.pucminas.graphtest.application.port.input.gce.records.ValidationGceOutput;
import br.pucminas.graphtest.application.service.gce.interfaces.GceMutationService;
import br.pucminas.graphtest.application.service.gce.interfaces.GceValidationResultService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

/**
 * Caso de uso responsavel por orquestrar a validacao de um GCE.
 */
public class ValidateGceUseCaseImpl implements ValidateGceUseCasePort {

    private final ProjectAccessService projectAccessService;
    private final GceValidationResultService gceValidationResultService;
    private final GceMutationService gceMutationService;

    /**
     * Cria o caso de uso com o servico responsavel pela validacao do modelo.
     *
     * @param gceValidationResultService servico que executa as verificacoes do GCE
     */
    public ValidateGceUseCaseImpl(ProjectAccessService projectAccessService,
                                  GceValidationResultService gceValidationResultService,
                                  GceMutationService gceMutationService) {
        this.projectAccessService = projectAccessService;
        this.gceValidationResultService = gceValidationResultService;
        this.gceMutationService = gceMutationService;
    }

    /**
     * Executa a validacao estrutural e semantica do grafo recebido.
     *
     * @param input dados do grafo a ser validado
     * @return resultado consolidado da validacao
     */
    @Override
    public ValidationGceOutput execute(ValidateGceInput input) {
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

        return gceValidationResultService.validate(graph);
    }
}
