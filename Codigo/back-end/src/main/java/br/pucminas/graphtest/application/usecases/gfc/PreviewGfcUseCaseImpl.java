package br.pucminas.graphtest.application.usecases.gfc;

import br.pucminas.graphtest.application.domain.gfc.model.Gfc;
import br.pucminas.graphtest.application.port.input.gfc.PreviewGfcUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.PreviewGfcInput;
import br.pucminas.graphtest.application.service.gfc.interfaces.GfcPreviewGenerationService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

/**
 * Caso de uso responsavel por gerar em memoria uma pre-visualizacao de GFC.
 */
public class PreviewGfcUseCaseImpl implements PreviewGfcUseCasePort {

    private final ProjectAccessService projectAccessService;
    private final GfcPreviewGenerationService gfcPreviewGenerationService;

    public PreviewGfcUseCaseImpl(ProjectAccessService projectAccessService,
                                 GfcPreviewGenerationService gfcPreviewGenerationService) {
        this.projectAccessService = projectAccessService;
        this.gfcPreviewGenerationService = gfcPreviewGenerationService;
    }

    @Override
    public GfcOutput execute(PreviewGfcInput input) {
        projectAccessService.findAuthorizedProject(input.projectId());
        Gfc graph = gfcPreviewGenerationService.generate(input);
        return GfcOutput.from(graph);
    }
}
