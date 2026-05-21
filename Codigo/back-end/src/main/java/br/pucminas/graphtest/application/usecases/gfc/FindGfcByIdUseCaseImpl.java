package br.pucminas.graphtest.application.usecases.gfc;

import br.pucminas.graphtest.application.domain.gfc.model.Gfc;
import br.pucminas.graphtest.application.exception.GfcNotFoundException;
import br.pucminas.graphtest.application.port.input.gfc.FindGfcByIdUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcOutput;
import br.pucminas.graphtest.application.port.output.repositories.GfcRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

import java.util.UUID;

/**
 * Caso de uso responsavel por recuperar um GFC persistido pelo identificador.
 */
public class FindGfcByIdUseCaseImpl implements FindGfcByIdUseCasePort {

    private final GfcRepositoryPort gfcRepositoryPort;
    private final ProjectAccessService projectAccessService;

    public FindGfcByIdUseCaseImpl(GfcRepositoryPort gfcRepositoryPort,
                                  ProjectAccessService projectAccessService) {
        this.gfcRepositoryPort = gfcRepositoryPort;
        this.projectAccessService = projectAccessService;
    }

    @Override
    public GfcOutput execute(UUID gfcId) {
        Gfc gfc = gfcRepositoryPort.findById(gfcId)
                .orElseThrow(GfcNotFoundException::new);
        projectAccessService.findAuthorizedProject(gfc.getProjectId());
        return GfcOutput.from(gfc);
    }
}
