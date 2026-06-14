package br.pucminas.graphtest.application.usecases.gfc;

import br.pucminas.graphtest.application.domain.gfc.model.GfcSourceFile;
import br.pucminas.graphtest.application.exception.GfcSourceFileNotFoundException;
import br.pucminas.graphtest.application.port.input.gfc.FindGfcSourceFileByIdUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceFileOutput;
import br.pucminas.graphtest.application.port.output.repositories.GfcSourceFileRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

import java.util.UUID;

public class FindGfcSourceFileByIdUseCaseImpl implements FindGfcSourceFileByIdUseCasePort {

    private final GfcSourceFileRepositoryPort gfcSourceFileRepositoryPort;
    private final ProjectAccessService projectAccessService;

    public FindGfcSourceFileByIdUseCaseImpl(GfcSourceFileRepositoryPort gfcSourceFileRepositoryPort,
                                            ProjectAccessService projectAccessService) {
        this.gfcSourceFileRepositoryPort = gfcSourceFileRepositoryPort;
        this.projectAccessService = projectAccessService;
    }

    @Override
    public GfcSourceFileOutput execute(UUID projectId, UUID sourceFileId) {
        GfcSourceFile sourceFile = gfcSourceFileRepositoryPort.findById(sourceFileId)
                .orElseThrow(GfcSourceFileNotFoundException::new);
        projectAccessService.findAuthorizedProject(sourceFile.getProjectId());
        if (!sourceFile.getProjectId().equals(projectId)) {
            throw new GfcSourceFileNotFoundException();
        }

        return GfcSourceFileOutput.from(sourceFile);
    }
}
