package br.pucminas.graphtest.application.port.input.gfc;

import br.pucminas.graphtest.application.port.input.gfc.records.GfcOutput;

import java.util.UUID;

/**
 * Porta de entrada do caso de uso responsavel por buscar um GFC pelo identificador.
 */
public interface FindGfcByIdUseCasePort {

    GfcOutput execute(UUID projectId, UUID gfcId);
}
