package br.pucminas.graphtest.application.usecases.gfc;

import br.pucminas.graphtest.application.domain.gfc.model.GfcSourceFile;
import br.pucminas.graphtest.application.exception.GfcSourceFileNotFoundException;
import br.pucminas.graphtest.application.port.input.gfc.GetGfcSourceCodeUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceCodeOutput;
import br.pucminas.graphtest.application.port.output.repositories.GfcSourceFileRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

import java.util.UUID;

/**
 * Caso de uso responsavel por recuperar o codigo-fonte Java persistido de um source-file GFC.
 */
public class GetGfcSourceCodeUseCaseImpl implements GetGfcSourceCodeUseCasePort {

    private final ProjectAccessService projectAccessService;
    private final GfcSourceFileRepositoryPort gfcSourceFileRepositoryPort;

    public GetGfcSourceCodeUseCaseImpl(ProjectAccessService projectAccessService,
                                       GfcSourceFileRepositoryPort gfcSourceFileRepositoryPort) {
        this.projectAccessService = projectAccessService;
        this.gfcSourceFileRepositoryPort = gfcSourceFileRepositoryPort;
    }

    @Override
    public GfcSourceCodeOutput execute(UUID sourceFileId) {
        GfcSourceFile sourceFile = gfcSourceFileRepositoryPort.findById(sourceFileId)
                .orElseThrow(GfcSourceFileNotFoundException::new);
        projectAccessService.findAuthorizedProject(sourceFile.getProjectId());

        return new GfcSourceCodeOutput(sourceFile.getContent());
    }
}
