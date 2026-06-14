package br.pucminas.graphtest.application.usecases.gfc;

import br.pucminas.graphtest.application.domain.gfc.model.GfcSourceFile;
import br.pucminas.graphtest.application.exception.GfcSourceFileNotFoundException;
import br.pucminas.graphtest.application.port.input.gfc.ListGfcSourceMethodsUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceMethodOutput;
import br.pucminas.graphtest.application.port.output.repositories.GfcSourceFileRepositoryPort;
import br.pucminas.graphtest.application.service.gfc.interfaces.GfcSourceMethodListingService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

import java.util.List;
import java.util.UUID;

/**
 * Caso de uso responsavel por listar metodos Java disponiveis para geracao de GFC.
 */
public class ListGfcSourceMethodsUseCaseImpl implements ListGfcSourceMethodsUseCasePort {

    private final ProjectAccessService projectAccessService;
    private final GfcSourceFileRepositoryPort gfcSourceFileRepositoryPort;
    private final GfcSourceMethodListingService gfcSourceMethodListingService;

    public ListGfcSourceMethodsUseCaseImpl(ProjectAccessService projectAccessService,
                                           GfcSourceFileRepositoryPort gfcSourceFileRepositoryPort,
                                           GfcSourceMethodListingService gfcSourceMethodListingService) {
        this.projectAccessService = projectAccessService;
        this.gfcSourceFileRepositoryPort = gfcSourceFileRepositoryPort;
        this.gfcSourceMethodListingService = gfcSourceMethodListingService;
    }

    @Override
    public List<GfcSourceMethodOutput> execute(UUID projectId, UUID sourceFileId) {
        GfcSourceFile sourceFile = gfcSourceFileRepositoryPort.findById(sourceFileId)
                .orElseThrow(GfcSourceFileNotFoundException::new);
        projectAccessService.findAuthorizedProject(sourceFile.getProjectId());
        if (!sourceFile.getProjectId().equals(projectId)) {
            throw new GfcSourceFileNotFoundException();
        }

        return gfcSourceMethodListingService.listMethods(sourceFile.getContent());
    }
}
