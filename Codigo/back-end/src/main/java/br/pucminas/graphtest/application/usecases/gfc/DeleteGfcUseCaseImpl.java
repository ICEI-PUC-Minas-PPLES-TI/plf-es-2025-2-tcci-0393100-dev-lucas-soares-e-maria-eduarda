package br.pucminas.graphtest.application.usecases.gfc;

import br.pucminas.graphtest.application.domain.gfc.model.Gfc;
import br.pucminas.graphtest.application.exception.GfcNotFoundException;
import br.pucminas.graphtest.application.port.input.gfc.DeleteGfcUseCasePort;
import br.pucminas.graphtest.application.port.output.repositories.GfcRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

import java.util.UUID;

/**
 * Caso de uso responsavel por remover um GFC persistido.
 */
public class DeleteGfcUseCaseImpl implements DeleteGfcUseCasePort {

    private final GfcRepositoryPort gfcRepositoryPort;
    private final ProjectAccessService projectAccessService;

    public DeleteGfcUseCaseImpl(GfcRepositoryPort gfcRepositoryPort,
                                ProjectAccessService projectAccessService) {
        this.gfcRepositoryPort = gfcRepositoryPort;
        this.projectAccessService = projectAccessService;
    }

    @Override
    public void execute(UUID gfcId) {
        Gfc gfc = gfcRepositoryPort.findById(gfcId)
                .orElseThrow(GfcNotFoundException::new);
        projectAccessService.findAuthorizedProject(gfc.getProjectId());
        gfcRepositoryPort.deleteById(gfc.getId());
    }
}
