package br.pucminas.graphtest.application.usecases.gfc;

import br.pucminas.graphtest.application.domain.gfc.model.GfcSourceFile;
import br.pucminas.graphtest.application.port.input.gfc.CreateGfcSourceFileUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.records.CreateGfcSourceFileInput;
import br.pucminas.graphtest.application.port.input.gfc.records.CreateGfcSourceFileOutput;
import br.pucminas.graphtest.application.port.output.repositories.GfcSourceFileRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

/**
 * Caso de uso responsavel por cadastrar arquivo-fonte Java para a feature GFC.
 */
public class CreateGfcSourceFileUseCaseImpl implements CreateGfcSourceFileUseCasePort {

    private final ProjectAccessService projectAccessService;
    private final GfcSourceFileRepositoryPort gfcSourceFileRepositoryPort;

    public CreateGfcSourceFileUseCaseImpl(ProjectAccessService projectAccessService,
                                          GfcSourceFileRepositoryPort gfcSourceFileRepositoryPort) {
        this.projectAccessService = projectAccessService;
        this.gfcSourceFileRepositoryPort = gfcSourceFileRepositoryPort;
    }

    @Override
    public CreateGfcSourceFileOutput execute(CreateGfcSourceFileInput input) {
        projectAccessService.findAuthorizedProject(input.projectId());

        GfcSourceFile sourceFile = GfcSourceFile.create(
                input.projectId(),
                input.fileName(),
                input.content()
        );

        GfcSourceFile savedSourceFile = gfcSourceFileRepositoryPort.save(sourceFile);
        return new CreateGfcSourceFileOutput(savedSourceFile.getId());
    }
}
