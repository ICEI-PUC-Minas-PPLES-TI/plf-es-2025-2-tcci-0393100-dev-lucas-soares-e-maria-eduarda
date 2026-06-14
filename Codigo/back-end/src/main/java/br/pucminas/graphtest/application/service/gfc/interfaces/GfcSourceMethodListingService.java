package br.pucminas.graphtest.application.service.gfc.interfaces;

import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceMethodOutput;

import java.util.List;

/**
 * Servico de aplicacao responsavel por listar metodos Java disponiveis para GFC.
 */
public interface GfcSourceMethodListingService {

    List<GfcSourceMethodOutput> listMethods(String sourceCode);
}
