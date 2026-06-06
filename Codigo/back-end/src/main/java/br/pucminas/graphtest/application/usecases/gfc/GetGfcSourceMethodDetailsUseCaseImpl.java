package br.pucminas.graphtest.application.usecases.gfc;

import br.pucminas.graphtest.application.domain.gfc.model.GfcSourceFile;
import br.pucminas.graphtest.application.exception.GfcSourceFileNotFoundException;
import br.pucminas.graphtest.application.port.input.gfc.GetGfcSourceMethodDetailsUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceMethodDetailsOutput;
import br.pucminas.graphtest.application.port.output.repositories.GfcSourceFileRepositoryPort;
import br.pucminas.graphtest.application.service.gfc.interfaces.GfcSourceMethodDetailsService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

import java.util.UUID;

public class GetGfcSourceMethodDetailsUseCaseImpl implements GetGfcSourceMethodDetailsUseCasePort {

    private final ProjectAccessService projectAccessService;
    private final GfcSourceFileRepositoryPort gfcSourceFileRepositoryPort;
    private final GfcSourceMethodDetailsService gfcSourceMethodDetailsService;

    public GetGfcSourceMethodDetailsUseCaseImpl(ProjectAccessService projectAccessService,
                                                GfcSourceFileRepositoryPort gfcSourceFileRepositoryPort,
                                                GfcSourceMethodDetailsService gfcSourceMethodDetailsService) {
        this.projectAccessService = projectAccessService;
        this.gfcSourceFileRepositoryPort = gfcSourceFileRepositoryPort;
        this.gfcSourceMethodDetailsService = gfcSourceMethodDetailsService;
    }

    @Override
    public GfcSourceMethodDetailsOutput execute(UUID projectId, UUID sourceFileId, String methodSignature) {
        GfcSourceFile sourceFile = gfcSourceFileRepositoryPort.findById(sourceFileId)
                .orElseThrow(GfcSourceFileNotFoundException::new);
        projectAccessService.findAuthorizedProject(sourceFile.getProjectId());
        if (!sourceFile.getProjectId().equals(projectId)) {
            throw new GfcSourceFileNotFoundException();
        }

        return gfcSourceMethodDetailsService.getDetails(sourceFile.getContent(), methodSignature);
    }
}
