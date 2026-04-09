package br.pucminas.graphtest.application.usecases.gce;

import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.gce.ValidateGceUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.records.ValidateGceByIdInput;
import br.pucminas.graphtest.application.port.input.gce.records.ValidationGceOutput;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.service.gce.interfaces.GceValidationResultService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

/**
 * Caso de uso responsavel por orquestrar a validacao de um GCE.
 */
public class ValidateGceUseCaseImpl implements ValidateGceUseCasePort {

    private final GceRepositoryPort gceRepository;
    private final ProjectAccessService projectAccessService;
    private final GceValidationResultService gceValidationResultService;

    /**
     * Cria o caso de uso com o servico responsavel pela validacao do modelo.
     *
     * @param gceValidationResultService servico que executa as verificacoes do GCE
     */
    public ValidateGceUseCaseImpl(GceRepositoryPort gceRepository,
                                  ProjectAccessService projectAccessService,
                                  GceValidationResultService gceValidationResultService) {
        this.gceRepository = gceRepository;
        this.projectAccessService = projectAccessService;
        this.gceValidationResultService = gceValidationResultService;
    }

    /**
     * Executa a validacao estrutural e semantica do grafo recebido.
     *
     * @param input dados contendo o identificador do grafo a ser validado
     * @return resultado consolidado da validacao
     */
    @Override
    public ValidationGceOutput execute(ValidateGceByIdInput input) {
        Gce graph = gceRepository.findById(input.id())
                .orElseThrow(() -> new EntityNotFoundException("GCE nao encontrado"));
        projectAccessService.findAuthorizedProject(graph.getProjectId());
        return gceValidationResultService.validate(graph);
    }
}
