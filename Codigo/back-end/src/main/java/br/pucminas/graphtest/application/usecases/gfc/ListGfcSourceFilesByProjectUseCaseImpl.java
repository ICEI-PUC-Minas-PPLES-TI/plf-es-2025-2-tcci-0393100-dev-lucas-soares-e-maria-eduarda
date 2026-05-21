package br.pucminas.graphtest.application.usecases.gfc;

import br.pucminas.graphtest.application.port.input.gfc.ListGfcSourceFilesByProjectUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceFileOutput;
import br.pucminas.graphtest.application.port.output.repositories.GfcSourceFileRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

import java.util.List;
import java.util.UUID;

public class ListGfcSourceFilesByProjectUseCaseImpl implements ListGfcSourceFilesByProjectUseCasePort {

    private final GfcSourceFileRepositoryPort gfcSourceFileRepositoryPort;
    private final ProjectAccessService projectAccessService;

    public ListGfcSourceFilesByProjectUseCaseImpl(GfcSourceFileRepositoryPort gfcSourceFileRepositoryPort,
                                                  ProjectAccessService projectAccessService) {
        this.gfcSourceFileRepositoryPort = gfcSourceFileRepositoryPort;
        this.projectAccessService = projectAccessService;
    }

    @Override
    public List<GfcSourceFileOutput> execute(UUID projectId) {
        projectAccessService.findAuthorizedProject(projectId);

        return gfcSourceFileRepositoryPort.findAllByProjectId(projectId).stream()
                .map(GfcSourceFileOutput::from)
                .toList();
    }
}
