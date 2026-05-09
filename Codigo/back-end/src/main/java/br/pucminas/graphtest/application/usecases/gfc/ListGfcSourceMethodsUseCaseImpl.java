package br.pucminas.graphtest.application.usecases.gfc;

import br.pucminas.graphtest.application.port.input.gfc.ListGfcSourceMethodsUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceMethodOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.ListGfcSourceMethodsInput;
import br.pucminas.graphtest.application.service.gfc.interfaces.GfcSourceMethodListingService;

import java.util.List;

/**
 * Caso de uso responsavel por listar metodos Java disponiveis para geracao de GFC.
 */
public class ListGfcSourceMethodsUseCaseImpl implements ListGfcSourceMethodsUseCasePort {

    private final GfcSourceMethodListingService gfcSourceMethodListingService;

    public ListGfcSourceMethodsUseCaseImpl(GfcSourceMethodListingService gfcSourceMethodListingService) {
        this.gfcSourceMethodListingService = gfcSourceMethodListingService;
    }

    @Override
    public List<GfcSourceMethodOutput> execute(ListGfcSourceMethodsInput input) {
        return gfcSourceMethodListingService.listMethods(input.sourceCode());
    }
}
