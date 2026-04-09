package br.pucminas.graphtest.application.usecases.gce;

import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.gce.FindGceByIdUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.records.FindGceByIdInput;
import br.pucminas.graphtest.application.port.input.gce.records.GceOutput;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

/**
 * Caso de uso responsavel por localizar um GCE por id.
 */
public class FindGceByIdUseCaseImpl implements FindGceByIdUseCasePort {

    private final GceRepositoryPort gceRepository;
    private final ProjectAccessService projectAccessService;

    /**
     * Cria o caso de uso com a dependencia necessaria para recuperar o GCE.
     *
     * @param gceRepository repositorio responsavel pela busca do agregado
     */
    public FindGceByIdUseCaseImpl(GceRepositoryPort gceRepository, ProjectAccessService projectAccessService) {
        this.gceRepository = gceRepository;
        this.projectAccessService = projectAccessService;
    }

    /**
     * Busca o GCE identificado na entrada.
     *
     * @param input dados contendo o identificador do grafo
     * @return representacao do GCE encontrado
     */
    @Override
    public GceOutput execute(FindGceByIdInput input) {
        Gce graph = gceRepository.findById(input.id())
                .orElseThrow(() -> new EntityNotFoundException("GCE nao encontrado"));
        projectAccessService.findAuthorizedProject(graph.getProjectId());

        return GceOutput.from(graph);
    }
}
