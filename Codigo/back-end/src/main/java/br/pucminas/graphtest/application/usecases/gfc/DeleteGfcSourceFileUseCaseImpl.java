package br.pucminas.graphtest.application.usecases.gfc;

import br.pucminas.graphtest.application.domain.gfc.model.GfcSourceFile;
import br.pucminas.graphtest.application.exception.GfcSourceFileNotFoundException;
import br.pucminas.graphtest.application.port.input.gfc.DeleteGfcSourceFileUseCasePort;
import br.pucminas.graphtest.application.port.output.repositories.GfcRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.GfcSourceFileRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

import java.util.UUID;

public class DeleteGfcSourceFileUseCaseImpl implements DeleteGfcSourceFileUseCasePort {

    private final GfcSourceFileRepositoryPort gfcSourceFileRepositoryPort;
    private final GfcRepositoryPort gfcRepositoryPort;
    private final ProjectAccessService projectAccessService;

    public DeleteGfcSourceFileUseCaseImpl(GfcSourceFileRepositoryPort gfcSourceFileRepositoryPort,
                                          GfcRepositoryPort gfcRepositoryPort,
                                          ProjectAccessService projectAccessService) {
        this.gfcSourceFileRepositoryPort = gfcSourceFileRepositoryPort;
        this.gfcRepositoryPort = gfcRepositoryPort;
        this.projectAccessService = projectAccessService;
    }

    @Override
    public void execute(UUID projectId, UUID sourceFileId) {
        GfcSourceFile sourceFile = gfcSourceFileRepositoryPort.findById(sourceFileId)
                .orElseThrow(GfcSourceFileNotFoundException::new);
        projectAccessService.findAuthorizedProject(sourceFile.getProjectId());
        if (!sourceFile.getProjectId().equals(projectId)) {
            throw new GfcSourceFileNotFoundException();
        }

        gfcRepositoryPort.deleteAllBySourceFileId(sourceFile.getId());
        gfcSourceFileRepositoryPort.deleteById(sourceFile.getId());
    }
}
