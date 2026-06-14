package br.pucminas.graphtest.application.usecases.gfc;

import br.pucminas.graphtest.application.domain.gfc.model.Gfc;
import br.pucminas.graphtest.application.domain.gfc.model.GfcSourceFile;
import br.pucminas.graphtest.application.exception.GfcSourceFileNotFoundException;
import br.pucminas.graphtest.application.exception.GfcSourceFileProjectMismatchException;
import br.pucminas.graphtest.application.port.input.gfc.CreateGfcUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.records.CreateGfcInput;
import br.pucminas.graphtest.application.port.input.gfc.records.CreateGfcOutput;
import br.pucminas.graphtest.application.port.output.repositories.GfcRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.GfcSourceFileRepositoryPort;
import br.pucminas.graphtest.application.service.gfc.interfaces.GfcGenerationService;
import br.pucminas.graphtest.application.service.gfc.records.GfcGenerationInput;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

/**
 * Caso de uso responsavel por criar e persistir um GFC a partir de um source-file cadastrado.
 */
public class CreateGfcUseCaseImpl implements CreateGfcUseCasePort {

    private final ProjectAccessService projectAccessService;
    private final GfcSourceFileRepositoryPort gfcSourceFileRepositoryPort;
    private final GfcGenerationService gfcGenerationService;
    private final GfcRepositoryPort gfcRepositoryPort;

    public CreateGfcUseCaseImpl(ProjectAccessService projectAccessService,
                                GfcSourceFileRepositoryPort gfcSourceFileRepositoryPort,
                                GfcGenerationService gfcGenerationService,
                                GfcRepositoryPort gfcRepositoryPort) {
        this.projectAccessService = projectAccessService;
        this.gfcSourceFileRepositoryPort = gfcSourceFileRepositoryPort;
        this.gfcGenerationService = gfcGenerationService;
        this.gfcRepositoryPort = gfcRepositoryPort;
    }

    @Override
    public CreateGfcOutput execute(CreateGfcInput input) {
        projectAccessService.findAuthorizedProject(input.projectId());
        GfcSourceFile sourceFile = gfcSourceFileRepositoryPort.findById(input.sourceFileId())
                .orElseThrow(GfcSourceFileNotFoundException::new);
        ensureSourceFileBelongsToProject(sourceFile, input);

        Gfc generatedGfc = gfcGenerationService.generate(new GfcGenerationInput(
                input.projectId(),
                input.sourceFileId(),
                sourceFile.getContent(),
                input.methodSignature(),
                input.name(),
                input.description()
        ));

        Gfc savedGfc = gfcRepositoryPort.save(generatedGfc);
        return new CreateGfcOutput(savedGfc.getId(), savedGfc.getCreatedAt());
    }

    private void ensureSourceFileBelongsToProject(GfcSourceFile sourceFile, CreateGfcInput input) {
        if (!sourceFile.getProjectId().equals(input.projectId())) {
            throw new GfcSourceFileProjectMismatchException();
        }
    }
}
