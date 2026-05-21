package br.pucminas.graphtest.application.usecases.gfc;

import br.pucminas.graphtest.application.port.input.gfc.ListGfcByProjectUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcSummaryOutput;
import br.pucminas.graphtest.application.port.output.repositories.GfcRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

import java.util.List;
import java.util.UUID;

/**
 * Caso de uso responsavel por listar GFCs pertencentes a um projeto.
 */
public class ListGfcByProjectUseCaseImpl implements ListGfcByProjectUseCasePort {

    private final GfcRepositoryPort gfcRepositoryPort;
    private final ProjectAccessService projectAccessService;

    public ListGfcByProjectUseCaseImpl(GfcRepositoryPort gfcRepositoryPort,
                                       ProjectAccessService projectAccessService) {
        this.gfcRepositoryPort = gfcRepositoryPort;
        this.projectAccessService = projectAccessService;
    }

    @Override
    public List<GfcSummaryOutput> execute(UUID projectId) {
        projectAccessService.findAuthorizedProject(projectId);
        return gfcRepositoryPort.findAllByProjectId(projectId).stream()
                .map(GfcSummaryOutput::from)
                .toList();
    }
}
